package org.brailleblaster.libembosser.drivers.indexBraille;

import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class IndexBrailleDocumentHandler implements DocumentHandler {
	public static class Builder {
		private int leftMargin = 0;
		private int topMargin = 0;
		private int cellsPerLine = 40;
		private int linesPerPage = 25;
		private int copies = 1;
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
		public IndexBrailleDocumentHandler build() {
			return new IndexBrailleDocumentHandler(leftMargin, topMargin, cellsPerLine, linesPerPage, copies);
		}
	}
	private final GenericTextDocumentHandler textHandler;
	private final ByteSource header;
	private IndexBrailleDocumentHandler(int leftMargin, int topMargin, int cellsPerLine, int linesPerPage, int copies) {
		this.textHandler = new GenericTextDocumentHandler.Builder()
				.setLeftMargin(leftMargin)
				.setTopMargin(topMargin)
				.setCellsPerLine(cellsPerLine)
				.setLinesPerPage(linesPerPage)
				.setCopies(1) // Our header will provide the copies escape sequence, so no data duplication needed.
				.build();
		String headerString = String.format("\u001bDBT0,MC%d,BI%d,CH%d,TM%d,LP%d;", copies, leftMargin, cellsPerLine, topMargin, linesPerPage);
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
