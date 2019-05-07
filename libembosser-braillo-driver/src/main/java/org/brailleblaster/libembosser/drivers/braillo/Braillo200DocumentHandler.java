package org.brailleblaster.libembosser.drivers.braillo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class Braillo200DocumentHandler extends AbstractBrailloDocumentHandler {
	public static class Builder {
		private int cellsPerLine = 40;
		private double sheetLength = 11.0;
		private int topMargin = 0;
		private int bottomMargin = 0;
		private int leftMargin = 0;
		private int rightMargin = 0;
		private boolean interpoint = false;
		private int copies = 1;
		private boolean zfolding;
		public Braillo200DocumentHandler build() {
			return new Braillo200DocumentHandler(cellsPerLine, sheetLength, topMargin, bottomMargin, leftMargin, rightMargin, interpoint, zfolding, copies);
		}

		public Builder setCellsperLine(int cellsPerLine) {
			checkArgument(10 <= cellsPerLine && cellsPerLine <= 42);
			checkState(checkLine(cellsPerLine, leftMargin, rightMargin));
			this.cellsPerLine = cellsPerLine;
			return this;
		}
		public Builder setSheetLength(double inches) {
			// Remember as rounding up to nearest half inch lower bound is 3.5
			checkArgument(3.5 < inches && inches <= 14.0, "Sheet length should be between 4 and 14 inches, given value is %d", inches);
			sheetLength = inches;
			return this;
		}
		public Builder setInterpoint(boolean interpoint) {
			this.interpoint = interpoint;
			return this;
		}

		public Builder setCopies(int copies) {
			checkArgument(copies > 0);
			this.copies = copies;
			return this;
		}

		public Builder setLeftMargin(int leftMargin) {
			checkArgument(leftMargin >= 0);
			checkState(checkLine(cellsPerLine, leftMargin, rightMargin));
			this.leftMargin = leftMargin;
			return this;
		}

		public Builder setTopMargin(int margin) {
			checkArgument(margin >= 0);
			this.topMargin = margin;
			return this;
		}
		public Builder setBottomMargin(int bottomMargin) {
			checkArgument(bottomMargin >= 0);
			this.bottomMargin = bottomMargin;
			return this;
		}

		public Builder setRightMargin(int rightMargin) {
			checkArgument(rightMargin >= 0);
			checkState(checkLine(cellsPerLine, leftMargin, rightMargin));
			this.rightMargin = rightMargin;
			return this;
		}

		private boolean checkLine(int cellsPerLine, int leftMargin, int rightMargin) {
			return rightMargin + leftMargin < cellsPerLine;
		}

		public Builder setZFolding(boolean zfolding) {
			this.zfolding = zfolding;
			return this;
		}
	}
	private ByteSource headerSource;
	private Braillo200DocumentHandler(int cellsPerLine, double sheetLength, int topMargin, int bottomMargin, int leftMargin, int rightMargin, boolean interpoint, boolean zfolding, int copies) {
		super(cellsPerLine, sheetLength, topMargin, bottomMargin, leftMargin, rightMargin, interpoint, copies);
		headerSource = ByteSource.wrap(String.format("\u001bS1\u001bJ0\u001bN0\u001bR0\u001bA%02d\u001bB%02d\u001bC%d\u001bH%d", (int)Math.ceil(sheetLength * 2), cellsPerLine, interpoint? 1:0, zfolding?1:0).getBytes(Charsets.US_ASCII));
	}
	@Override
	protected ByteSource getHeader() {
		return headerSource;
	}
}
