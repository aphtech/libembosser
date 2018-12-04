package org.brailleblaster.libembosser.drivers.indexBraille;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.OptionalInt;

import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;
import org.brailleblaster.libembosser.spi.MultiSides;

import com.google.common.base.Charsets;
import com.google.common.collect.Streams;
import com.google.common.io.ByteSource;

public class IndexBrailleDocumentHandler implements DocumentToByteSourceHandler {
	public static class Builder {
		private int leftMargin = 0;
		private int topMargin = 0;
		private int cellsPerLine = 40;
		private int linesPerPage = 25;
		private int copies = 1;
		private int paperMode = 1;
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
			this.paperMode = mode;
			return this;
		}
		public Builder setPaperMode(MultiSides sides) {
			switch(sides) {
			case P1ONLY:
			case P2ONLY:
				setPaperMode(1);
				break;
			case INTERPOINT:
				setPaperMode(2);
				break;
			case Z_FOLDING_DOUBLE_HORIZONTAL:
				setPaperMode(3);
				break;
			case SADDLE_STITCH_DOUBLE_SIDED:
				setPaperMode(4);
				break;
			case Z_FOLDING_SINGLE_HORIZONTAL:
				setPaperMode(5);
				break;
			case Z_FOLDING_DOUBLE_VERTICAL:
				setPaperMode(6);
				break;
			case Z_FOLDING_SINGLE_VERTICAL:
				setPaperMode(7);
				break;
			case SADDLE_STITCH_SINGLE_SIDED:
				setPaperMode(8);
				break;
			default:
				throw new IllegalArgumentException(String.format("Index embossers do not support %s paper mode", sides.name()));
			}
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
	private final GenericTextDocumentHandler textHandler;
	private final ByteSource header;
	private IndexBrailleDocumentHandler(int leftMargin, int topMargin, int cellsPerLine, int linesPerPage, int paperMode, OptionalInt paperSize, int copies) {
		this.textHandler = new GenericTextDocumentHandler.Builder()
				.setLeftMargin(0) // Left margin is handled by the escape sequences and needs no padding
				.setTopMargin(0) // Top margin handled by escape sequence and need not be padded.
				.setCellsPerLine(cellsPerLine)
				.setLinesPerPage(linesPerPage)
				.setCopies(1) // Our header will provide the copies escape sequence, so no data duplication needed.
				.build();
		String paperParam = Streams.stream(paperSize).mapToObj(v -> String.format("PA%d,", v)).findFirst().orElse("");
		String headerString = String.format("\u001bDBT0,MC%d,DP%d,%sBI%d,CH%d,TM%d,LP%d;", copies, paperMode, paperParam, leftMargin, cellsPerLine, topMargin, linesPerPage);
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
