package org.brailleblaster.libembosser.drivers.generic;

import java.io.ByteArrayOutputStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiConsumer;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;
import org.brailleblaster.libembosser.utils.BrailleMapper;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class GenericTextDocumentHandler implements DocumentHandler {
	private static void throwInvalidStateException(DocumentEvent event, String state) {
		throw new IllegalStateException(String.format("Invalid event %s for state %s", event.getClass().getName(), state));
	}
	private enum HandlerStates {
		READY((h, e) -> {
			if (e instanceof StartDocumentEvent) {
				h.startDocument(((StartDocumentEvent)e).getOptions());
			} else {
				throwInvalidStateException(e, "READY");
			}
		}),
		DOCUMENT((h, e) -> {
			if (e instanceof StartVolumeEvent) {
				h.startVolume(((StartVolumeEvent)e).getOptions());
			} else if (e instanceof EndDocumentEvent) {
				h.endDocument();
			} else {
				throwInvalidStateException(e, "DOCUMENT");
			}
		}),
		VOLUME((h, e) -> {
			if (e instanceof StartSectionEvent) {
				h.startSection(((StartSectionEvent)e).getOptions());
			} else if (e instanceof EndVolumeEvent) {
				h.endVolume();
			} else {
				throwInvalidStateException(e, "VOLUME");
			}
		}),
		SECTION((h, e) -> {
			if (e instanceof StartPageEvent) {
				h.startPage(((StartPageEvent)e).getOptions());
			} else if (e instanceof EndSectionEvent) {
				h.endSection();
			} else {
				throwInvalidStateException(e, "SECTION");
			}
		}),
		PAGE((h, e) -> {
			if (e instanceof StartLineEvent) {
				h.startLine(((StartLineEvent)e).getOptions());
			} else if (e instanceof EndPageEvent) {
				h.endPage();
			} else {
				throwInvalidStateException(e, "PAGE");
			}
		}),
		LINE((h, e) -> {
			if (e instanceof BrailleEvent) {
				h.writeBraille(((BrailleEvent)e).getBraille());
			} else if (e instanceof EndLineEvent) {
				h.endLine();
			} else {
				throwInvalidStateException(e, "LINE");
			}
		});
		private BiConsumer<GenericTextDocumentHandler, DocumentEvent> f;
		private HandlerStates(BiConsumer<GenericTextDocumentHandler, DocumentEvent> f) {
			this.f = f;
		}
		public void accept(GenericTextDocumentHandler h, DocumentEvent e) {
			f.accept(h, e);
		}
	}
	public final static class Builder {
		private int cellsPerLine = 40;
		private int linesPerPage = 25;
		private boolean interpoint = false;
		public Builder setCellsPerLine(int cellsPerLine) {
			this.cellsPerLine = cellsPerLine;
			return this;
		}
		public Builder setLinesPerPage(int linesPerPage) {
			this.linesPerPage = linesPerPage;
			return this;
		}
		public Builder setInterpoint(boolean interpoint) {
			this.interpoint = interpoint;
			return this;
		}
		public GenericTextDocumentHandler build() {
			return new GenericTextDocumentHandler(cellsPerLine, linesPerPage, interpoint);
		}
	}
	private int pageNum = 0;
	private ByteArrayOutputStream output;
	private final int initialBufferCapacity;
	private final int defaultCellsPerLine;
	private final int defaultLinesPerPage;
	private final boolean defaultInterpoint;
	private final int defaultRowGap = 0;
	private int linesRemaining = 0;
	private int cellsPerLine;
	private int cellsRemaining = 0;
	private final Deque<Set<? extends Option>> optionStack = new LinkedList<>();
	private final Deque<HandlerStates> stateStack = new LinkedList<>();
	private final byte[] newLineBytes;
	private byte[] newPageBytes;

	private GenericTextDocumentHandler(int cellsPerLine, int linesPerPage, boolean interpoint) {
		defaultCellsPerLine = cellsPerLine;
		this.cellsPerLine = defaultCellsPerLine;
		defaultLinesPerPage = linesPerPage;
		defaultInterpoint = interpoint;
		initialBufferCapacity = 1000000;
		output = new ByteArrayOutputStream(initialBufferCapacity);
		newLineBytes = new byte[] {'\r','\n'};
		newPageBytes = new byte[] {'\f'};
	}
	
	@Override
	public void onEvent(DocumentEvent event) {
		HandlerStates state = stateStack.isEmpty()? HandlerStates.READY : stateStack.peek();
		state.accept(this, event);
	}
	
	public void startDocument(Set<DocumentOption> options) {
		pageNum = 0;
		output.reset();
		// Reset the option stack, in case of previous failure.
		optionStack.clear();
		// Push the options to the stack
		optionStack.push(options);;
		stateStack.push(HandlerStates.DOCUMENT);
	}
	
	public void endDocument() {
		// Remove document options from the stack
		optionStack.pop();
		stateStack.pop();
	}
	
	public void startVolume(Set<VolumeOption> options) {
		// Push the volume options to the stack
		optionStack.push(options);
		stateStack.push(HandlerStates.VOLUME);
	}

	public void endVolume() {
		// Remove the volume options
		optionStack.pop();
		stateStack.pop();
	}
	
	public void startSection(Set<SectionOption> options) {
		optionStack.push(options);
		stateStack.push(HandlerStates.SECTION);
	}
	
	public void endSection() {
		optionStack.pop();
		stateStack.pop();
	}

	public void startPage(Set<PageOption> options) {
		optionStack.push(options);
		// Get the linesPerPage
		linesRemaining = optionStack.stream().flatMap(o -> o.stream()).filter(o -> o instanceof LinesPerPage).findFirst().map(o -> ((LinesPerPage)o).getValue()).orElse(defaultLinesPerPage) - 1;
		cellsPerLine = optionStack.stream().flatMap(o -> o.stream()).filter(o -> o instanceof CellsPerLine).findFirst().map(o -> ((CellsPerLine)o).getValue()).orElse(defaultCellsPerLine);
		// When at the start of the document we do not insert a form feed.
		// Assume embosser is already on a new page due to starting a new job.
		if (pageNum > 0) {
			write(newPageBytes);
		}
		pageNum++;
		stateStack.push(HandlerStates.PAGE);
	}
	
	public void endPage() {
		// Pad the page with new lines to make it contain linesPerPage.
		if (linesRemaining > 0) {
			write(repeatedBytes(newLineBytes, linesRemaining));
		}
		optionStack.pop();
		stateStack.pop();
	}

	private byte[] repeatedBytes(byte[] inBytes, int count) {
		byte[] buf = new byte[count * inBytes.length];
		for (int i = 0; i < buf.length; i += newLineBytes.length) {
			System.arraycopy(inBytes, 0, buf, i, inBytes.length);
		}
		return buf;
	}

	public void startLine(Set<RowOption> options) {
		optionStack.push(options);
		cellsRemaining = cellsPerLine;
		stateStack.push(HandlerStates.LINE);
	}

	public void endLine() {
		int rowGap = optionStack.stream().flatMap(o -> o.stream()).filter(o -> o instanceof RowGap).findFirst().map(o -> ((RowGap)o).getValue()).orElse(defaultRowGap) + 1;
		int newLines = Math.min(linesRemaining, rowGap);
		if (newLines > 0) {
			write(repeatedBytes(newLineBytes, newLines));
		}
		linesRemaining -= rowGap;
		optionStack.pop();
		stateStack.pop();
	}

	public void writeBraille(String braille) {
		if (linesRemaining >= 0) {
			String asciiBraille = BrailleMapper.UNICODE_TO_ASCII_FAST.map(braille.substring(0, Math.min(braille.length(), cellsRemaining)));
			write(asciiBraille.getBytes(Charsets.UTF_8));
			cellsRemaining -= asciiBraille.length();
		}
	}
	private void write(byte[] bytes) {
		output.write(bytes, 0, bytes.length);
	}
	public ByteSource asByteSource() {
		return ByteSource.wrap(output.toByteArray());
	}
}
