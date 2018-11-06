package org.brailleblaster.libembosser.drivers.indexBraille;

import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;
import org.brailleblaster.libembosser.spi.MultiSides;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class IndexBrailleDocumentHandler implements DocumentHandler {
	public static class Builder {
		private int leftMargin = 0;
		private int topMargin = 0;
		private int cellsPerLine = 40;
		private int linesPerPage = 25;
		private int copies = 1;
		private int paperMode = 1;
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
		public Builder setPaperMode(MultiSides sides) {
			switch(sides) {
			case P1ONLY:
			case P2ONLY:
				paperMode = 1;
				break;
			case INTERPOINT:
				paperMode = 2;
				break;
			case Z_FOLDING_DOUBLE_HORIZONTAL:
				paperMode = 3;
				break;
			case SADDLE_STITCH_DOUBLE_SIDED:
				paperMode = 4;
				break;
			case Z_FOLDING_SINGLE_HORIZONTAL:
				paperMode = 5;
				break;
			case Z_FOLDING_DOUBLE_VERTICAL:
				paperMode = 6;
				break;
			case Z_FOLDING_SINGLE_VERTICAL:
				paperMode = 7;
				break;
			case SADDLE_STITCH_SINGLE_SIDED:
				paperMode = 8;
				break;
			default:
				throw new IllegalArgumentException(String.format("Index embossers do not support %s paper mode", sides.name()));
			}
			return this;
		}
		public IndexBrailleDocumentHandler build() {
			return new IndexBrailleDocumentHandler(leftMargin, topMargin, cellsPerLine, linesPerPage, paperMode, copies);
		}
	}
	private final GenericTextDocumentHandler textHandler;
	private final ByteSource header;
	private IndexBrailleDocumentHandler(int leftMargin, int topMargin, int cellsPerLine, int linesPerPage, int paperMode, int copies) {
		this.textHandler = new GenericTextDocumentHandler.Builder()
				.setLeftMargin(leftMargin)
				.setTopMargin(topMargin)
				.setCellsPerLine(cellsPerLine)
				.setLinesPerPage(linesPerPage)
				.setCopies(1) // Our header will provide the copies escape sequence, so no data duplication needed.
				.build();
		String headerString = String.format("\u001bDBT0,MC%d,DP%d,BI%d,CH%d,TM%d,LP%d;", copies, paperMode, leftMargin, cellsPerLine, topMargin, linesPerPage);
		header = ByteSource.wrap(headerString.getBytes(Charsets.US_ASCII));
	}
	@Override
	public void onEvent(DocumentEvent event) {
		textHandler.onEvent(event);
	}
	public ByteSource asByteSource() {
		return ByteSource.concat(header, textHandler.asByteSource());
	}
}
