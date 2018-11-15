package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import static com.google.common.base.Preconditions.checkArgument;
import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

public class EnablingTechnologiesDocumentHandler implements DocumentHandler {
	public static class Builder {
		private int leftMargin = 0;
		private int cellsPerLine = 40;
		private int topMargin = 0;
		private int linesPerPage = 25;
		private int copies = 1;
		private int pageLength = 11;
		
		public EnablingTechnologiesDocumentHandler build() {
			return new EnablingTechnologiesDocumentHandler(leftMargin, cellsPerLine, topMargin, pageLength, linesPerPage, copies);
		}

		public Builder setCellsPerLine(int cellsPerLine) {
			checkNumberArgument(cellsPerLine);
			this.cellsPerLine = cellsPerLine;
			return this;
		}

		public Builder setCopies(int copies) {
			this.copies = copies;
			return this;
		}

		public Builder setLeftMargin(int leftMargin) {
			checkNumberArgument(leftMargin);
			this.leftMargin = leftMargin;
			return this;
		}

		public Builder setLinesPerPage(int linesPerPage) {
			checkNumberArgument(linesPerPage);
			this.linesPerPage = linesPerPage;
			return this;
		}
		
		public Builder setTopMargin(int topMargin) {
			checkNumberArgument(topMargin);
			this.topMargin = topMargin;
			return this;
		}

		public Builder setPageLength(int inches) {
			checkNumberArgument(inches);
			this.pageLength = inches;
			return this;
		}
	}
	
	private static final byte[] NUMBER_MAPPING = new byte[] { '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', '{' };
	private static boolean isNumberArgValid(int value) {
		return value >= 0 && value < NUMBER_MAPPING.length;
	}
	
	private static void checkNumberArgument(int cellsPerLine) {
		checkArgument(isNumberArgValid(cellsPerLine), "Argument not in valid range, must be between {} and {} but is {}", 0, NUMBER_MAPPING.length - 1, cellsPerLine);
	}

	private ByteSource headerSource;
	private GenericTextDocumentHandler handler;
	private EnablingTechnologiesDocumentHandler(int leftMargin, int cellsPerLine, int topMargin, int pageLength, int linesPerPage, int copies) {
		this.handler = new GenericTextDocumentHandler.Builder()
				.setLeftMargin(0)
				.setCellsPerLine(cellsPerLine)
				.setTopMargin(topMargin)
				.setLinesPerPage(linesPerPage)
				.setCopies(copies)
				.build();
		// Build the header
		ByteArrayDataOutput headerOutput = ByteStreams.newDataOutput(100);
		headerOutput.write(new byte[] {0x1b, '@'}); // Reset
		headerOutput.write(new byte[] {0x1b, 'A', '@', '@'}); // Set Braille tables
		headerOutput.write(new byte[] {0x1b, 'K', '@'}); // Set 6-dot mode
		headerOutput.write(new byte[] {0x1b, 'W', '@'}); // Line wrapping
		headerOutput.write(new byte[] {0x1b, 'i', '@'}); // Interpoint mode
		headerOutput.write(new byte[] {0x1b, 's', '@'}); // Braille cell type
		headerOutput.write(new byte[] {0x1b, 'L', NUMBER_MAPPING[leftMargin]}); // Set left margin
		headerOutput.write(new byte[] {0x1b, 'R', NUMBER_MAPPING[cellsPerLine]}); // Set cells per line
		headerOutput.write(new byte[] {0x1b, 'T', NUMBER_MAPPING[pageLength]});
		headerOutput.write(new byte[] {0x1b, 'Q', NUMBER_MAPPING[topMargin + linesPerPage]}); // Set lines per page, include top margin as this needs padding
		this.headerSource = ByteSource.wrap(headerOutput.toByteArray());
	}

	@Override
	public void onEvent(DocumentEvent event) {
		handler.onEvent(event);
	}
	
	public ByteSource asByteSource() {
		return ByteSource.concat(headerSource, handler.asByteSource());
	}

}
