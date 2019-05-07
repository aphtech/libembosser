package org.brailleblaster.libembosser.drivers.braillo;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class Braillo270DocumentHandler extends AbstractBrailloDocumentHandler {
	public static class Builder {
		public Braillo270DocumentHandler build() {
			return new Braillo270DocumentHandler(0, 0.0, 0, 0, 0, 0, false, 1);
		}
	}
	private ByteSource headerSource;
	private Braillo270DocumentHandler(int cellsPerLine, double sheetLength, int topMargin, int bottomMargin, int leftMargin, int rightMargin, boolean interpoint, int copies) {
		super(cellsPerLine, sheetLength, topMargin, bottomMargin, leftMargin, rightMargin, interpoint, copies);
		headerSource = ByteSource.wrap("\u001bE\u001bA\u001b6".getBytes(Charsets.US_ASCII));
	}
	@Override
	protected ByteSource getHeader() {
		return headerSource;
	}
}