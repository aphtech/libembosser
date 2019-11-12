package org.brailleblaster.libembosser.drivers.indexBraille;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.OptionalInt;

import org.brailleblaster.libembosser.drivers.utils.document.ByteSourceHandlerToFunctionAdapter;
import org.brailleblaster.libembosser.drivers.utils.document.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.spi.Layout;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.google.common.io.ByteSource;

public class IndexBrailleDocumentHandler implements ByteSourceHandlerToFunctionAdapter {
	public static class Builder {
		private int leftMargin = 0;
		private int topMargin = 0;
		private int cellsPerLine = 40;
		private int linesPerPage = 25;
		private int copies = 1;
		private Layout paperMode = Layout.P1ONLY;
		private OptionalInt paperSize = OptionalInt.empty();
		public Builder setLeftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
			return this;
		}
		public Builder setCellsPerLine(int cellsPerLine) {
			this.cellsPerLine = cellsPerLine;
			return this;
		}
		public Builder setTopMargin(int topMargin) {
			this.topMargin = topMargin;
			return this;
		}
		public Builder setLinesPerPage(int linesPerPage) {
			this.linesPerPage = linesPerPage;
			return this;
		}
		public Builder setCopies(int copies) {
			this.copies = copies;
			return this;
		}
		public Builder setPaperMode(int mode) {
			checkArgument(PAPER_MODE_MAPPINGS.containsValue(mode), String.format("Index embossers do not support %s paper mode", mode));
			this.paperMode = PAPER_MODE_MAPPINGS.entrySet().stream().filter(e -> e.getValue() == mode).findFirst().map(e -> e.getKey()).get();
			return this;
		}
		public Builder setPaperMode(Layout sides) {
			checkArgument(PAPER_MODE_MAPPINGS.containsKey(sides), String.format("Index embossers do not support %s paper mode", sides.name()));
			paperMode = sides;
			return this;
		}
		public Builder setPaper(OptionalInt paper) {
			checkNotNull(paper);
			this.paperSize = paper;
			return this;
		}
		public IndexBrailleDocumentHandler build() {
			return new IndexBrailleDocumentHandler(leftMargin, topMargin, cellsPerLine, linesPerPage, paperMode, paperSize, copies);
		}
	}
	private final static ImmutableMap<Layout, Integer> PAPER_MODE_MAPPINGS = new ImmutableMap.Builder<Layout, Integer>().put(Layout.P1ONLY, 1).put(Layout.P2ONLY, 1).put(Layout.INTERPOINT, 2).put(Layout.Z_FOLDING_DOUBLE_HORIZONTAL, 3).put(Layout.SADDLE_STITCH_DOUBLE_SIDED, 4).put(Layout.Z_FOLDING_SINGLE_HORIZONTAL, 5).put(Layout.Z_FOLDING_DOUBLE_VERTICAL, 6).put(Layout.Z_FOLDING_SINGLE_VERTICAL, 7).put(Layout.SADDLE_STITCH_SINGLE_SIDED, 8).build();
	private final GenericTextDocumentHandler textHandler;
	private final ByteSource header;
	private IndexBrailleDocumentHandler(int leftMargin, int topMargin, int cellsPerLine, int linesPerPage, Layout paperMode, OptionalInt paperSize, int copies) {
		this.textHandler = new GenericTextDocumentHandler.Builder()
				.setLeftMargin(0) // Left margin is handled by the escape sequences and needs no padding
				.setTopMargin(0) // Top margin handled by escape sequence and need not be padded.
				.setCellsPerLine(cellsPerLine)
				.setLinesPerPage(linesPerPage)
				.setCopies(1) // Our header will provide the copies escape sequence, so no data duplication needed.
				.setInterpoint(paperMode.isDoubleSide())
				.setFooter(new byte[] {0x1a})
				.build();
		String paperParam = Streams.stream(paperSize).mapToObj(v -> String.format("PA%d,", v)).findFirst().orElse("");
		// 2019-11-12: For now we are ignoring the margins for Index embossers.
		String headerString = String.format("\u001bDBT0,MC%d,DP%d,%sBI%d,CH%d,TM%d,LP%d;", copies, PAPER_MODE_MAPPINGS.get(paperMode), paperParam, 0, cellsPerLine, 0, linesPerPage);
		header = ByteSource.wrap(headerString.getBytes(Charsets.US_ASCII));
	}
	@Override
	public void onEvent(DocumentEvent event) {
		textHandler.onEvent(event);
	}
	@Override
	public ByteSource asByteSource() {
		return ByteSource.concat(header, textHandler.asByteSource());
	}
}
