package org.brailleblaster.libembosser.drivers.braillo;

import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;
import org.brailleblaster.libembosser.drivers.utils.document.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;

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
				.setEndOfPage(new byte[] { '\r', '\n', '\f'})
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
