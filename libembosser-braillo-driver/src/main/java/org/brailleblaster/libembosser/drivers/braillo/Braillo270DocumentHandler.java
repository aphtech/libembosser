package org.brailleblaster.libembosser.drivers.braillo;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class Braillo270DocumentHandler extends AbstractBrailloDocumentHandler {
	public static class Builder {
		private int cellsPerLine = 40;
		private double sheetLength = 11.0;
		public Braillo270DocumentHandler build() {
			return new Braillo270DocumentHandler(cellsPerLine, sheetLength, 0, 0, 0, 0, false, 1);
		}

		public Builder setCellsPerLine(int cellsPerLine) {
			checkArgument(27 <= cellsPerLine && cellsPerLine <= 42, "Cells per line invalid %s, valid range is 27 <= cells per line <= 42", cellsPerLine);
			this.cellsPerLine = cellsPerLine;
			return this;
		}

		public Builder setSheetlength(double sheetLength) {
			checkArgument(9.5 < sheetLength && sheetLength <= 14.0, "Sheet length invalid %s, valid range is 9.5 < sheet length <= 14.0", sheetLength);
			this.sheetLength = sheetLength;
			return this;
		}
	}
	private ByteSource headerSource;
	private Braillo270DocumentHandler(int cellsPerLine, double sheetLength, int topMargin, int bottomMargin, int leftMargin, int rightMargin, boolean interpoint, int copies) {
		super(cellsPerLine, sheetLength, topMargin, bottomMargin, leftMargin, rightMargin, interpoint, copies);
		String cells = Integer.toHexString(cellsPerLine - 27).toUpperCase();
		int sl = ((int)Math.ceil(sheetLength * 2)) - 20;
		headerSource = ByteSource.wrap(String.format("\u001bE\u001bA\u001b6\u001b\u001E%1d\u001b\u001f%s", sl, cells).getBytes(Charsets.US_ASCII));
	}
	@Override
	protected ByteSource getHeader() {
		return headerSource;
	}
}