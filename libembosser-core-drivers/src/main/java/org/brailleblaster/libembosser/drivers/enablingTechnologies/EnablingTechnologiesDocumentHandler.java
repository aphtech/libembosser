package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;

import com.google.common.io.ByteSource;

public class EnablingTechnologiesDocumentHandler implements DocumentHandler {
	public static class Builder {
		private int leftMargin = 0;
		private int cellsPerLine = 40;
		private int topMargin = 0;
		private int linesPerPage = 25;
		private int copies = 1;
		
		public EnablingTechnologiesDocumentHandler build() {
			return new EnablingTechnologiesDocumentHandler(leftMargin, cellsPerLine, topMargin, linesPerPage, copies);
		}

		public Builder setCellsPerLine(int cellsPerLine) {
			this.cellsPerLine = cellsPerLine;
			return this;
		}

		public Builder setCopies(int copies) {
			this.copies = copies;
			return this;
		}

		public Builder setLeftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
			return this;
		}

		public Builder setLinesPerPage(int linesPerPage) {
			this.linesPerPage = linesPerPage;
			return this;
		}
	}
	
	private GenericTextDocumentHandler handler;
	private EnablingTechnologiesDocumentHandler(int leftMargin, int cellsPerLine, int topMargin, int linesPerPage, int copies) {
		this.handler = new GenericTextDocumentHandler.Builder()
				.setLeftMargin(0)
				.setCellsPerLine(cellsPerLine)
				.setTopMargin(0)
				.setLinesPerPage(linesPerPage)
				.setCopies(copies)
				.build();
	}

	@Override
	public void onEvent(DocumentEvent event) {
		handler.onEvent(event);
	}
	
	public ByteSource asByteSource() {
		return handler.asByteSource();
	}

}
