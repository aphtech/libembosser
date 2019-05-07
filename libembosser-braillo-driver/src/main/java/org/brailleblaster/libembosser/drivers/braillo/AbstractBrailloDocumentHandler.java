package org.brailleblaster.libembosser.drivers.braillo;

import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

public abstract class AbstractBrailloDocumentHandler implements DocumentToByteSourceHandler {
	private GenericTextDocumentHandler handler;
	protected AbstractBrailloDocumentHandler(int cellsPerLine, double sheetLength, int topMargin, int bottomMargin, int leftMargin, int rightMargin, boolean interpoint, int copies) {
		int linesPerPage = (int)Math.floor(sheetLength * 2.54);
		handler = new GenericTextDocumentHandler.Builder()
				.setTopMargin(topMargin)
				.setLeftMargin(leftMargin)
				.setCellsPerLine(cellsPerLine - leftMargin - rightMargin)
				.setLinesPerPage(linesPerPage - topMargin - bottomMargin)
				.padWithBlankLines(true)
				.setEndOfPage(String.format("%s\f", Strings.repeat("\r\n", bottomMargin + 1)).getBytes(Charsets.US_ASCII))
				.setInterpoint(interpoint)
				.setCopies(copies)
				.build();
	}
	protected abstract ByteSource getHeader();
	@Override
	public void onEvent(DocumentEvent event) {
		handler.onEvent(event);
	}
	@Override
	public ByteSource asByteSource() {
		// TODO Auto-generated method stub
		return ByteSource.concat(getHeader(), handler.asByteSource());
	}
}
