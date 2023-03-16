/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.braillo;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class Braillo270DocumentHandler extends AbstractBrailloDocumentHandler {
	public enum Firmware {
		V1_11(9.5, 14.0) {
			@Override
			public ByteSource getHeader(int cellsPerLine, double sheetLength, boolean interpoint, boolean zfolding) {
				String cells = Integer.toHexString(cellsPerLine - 27).toUpperCase();
				int sl = ((int)Math.ceil(sheetLength * 2)) - 20;
				return ByteSource.wrap(String.format("\u001bE\u001bA\u001b6\u001b\u001E%1d\u001b\u001f%s", sl, cells).getBytes(Charsets.US_ASCII));
			}
		},
		V12_16(3.5, 14.0) {
			String[] lengths = new String[] { "0", "1", "1", "2", "2", "3", "3", "4", "4", "5", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
			@Override
			public ByteSource getHeader(int cellsPerLine, double sheetLength, boolean interpoint, boolean zfolding) {
				String cells = Integer.toHexString(cellsPerLine - 27).toUpperCase();
				int sl = ((int)Math.ceil(sheetLength * 2)) - 8;
				return ByteSource.wrap(String.format("\u001bE\u001bA\u001b6\u001b\u001E%s\u001b\u001f%s\u001bS%s\u001bQ%s", lengths[sl], cells, interpoint? '1': '0', zfolding? '1': '0').getBytes(Charsets.US_ASCII));
			}
		};
		final private double minSheetLength;
		final private double maxSheetLength;
		Firmware(double minSheet, double maxSheet) {
			this.minSheetLength = minSheet;
			this.maxSheetLength = maxSheet;
		}
		public abstract ByteSource getHeader(int cellsPerLine, double sheetLength, boolean interpoint, boolean zfolding);
		public boolean isValidSheetLength(double sheetLength) {
			return minSheetLength < sheetLength && sheetLength <= maxSheetLength;
		}
		public double getMinSheetLength() {
			return minSheetLength;
		}
		public double getMaxSheetLength() {
			return maxSheetLength;
		}
	}
	public static class Builder {
		private final Firmware firmware;
		private int cellsPerLine = 40;
		private double sheetLength = 11.0;
		private int leftMargin = 0;
		private int rightMargin = 0;
		private int topMargin = 0;
		private int bottomMargin = 0;
		private boolean interpoint = true;
		private boolean zfolding = false;
		private int copies = 1;
		public Builder(Firmware firmware) {
			this.firmware = firmware;
		}
		public Braillo270DocumentHandler build() {
			return new Braillo270DocumentHandler(firmware, cellsPerLine, sheetLength, topMargin, bottomMargin, leftMargin, rightMargin, interpoint, zfolding, copies);
		}
		public Builder setCopies(int copies) {
			checkArgument(copies > 0);
			this.copies = copies;
			return this;
		}
		public Builder setCellsPerLine(int cellsPerLine) {
			checkArgument(27 <= cellsPerLine && cellsPerLine <= 42, "Cells per line invalid %s, valid range is 27 <= cells per line <= 42", cellsPerLine);
			this.cellsPerLine = cellsPerLine;
			return this;
		}

		public Builder setSheetlength(double sheetLength) {
			checkArgument(firmware.isValidSheetLength(sheetLength), "Sheet length invalid %s, valid range is %s < sheet length <= %s", sheetLength, firmware.getMinSheetLength(), firmware.getMaxSheetLength());
			this.sheetLength = sheetLength;
			return this;
		}
		public Builder setTopMargin(int topMargin) {
			this.topMargin = topMargin;
			return this;
		}
		public Builder setBottomMargin(int bottomMargin) {
			this.bottomMargin = bottomMargin;
			return this;
		}
		public Builder setLeftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
			return this;
		}
		public Builder setRightMargin(int rightMargin) {
			this.rightMargin = rightMargin;
			return this;
		}
		public Builder setInterpoint(boolean interpoint) {
			this.interpoint = interpoint;
			return this;
		}
		public Builder setZFolding(boolean zfolding) {
			this.zfolding = zfolding;
			return this;
		}
	}
	private ByteSource headerSource;
	private Braillo270DocumentHandler(Firmware firmware, int cellsPerLine, double sheetLength, int topMargin, int bottomMargin, int leftMargin, int rightMargin, boolean interpoint, boolean zfolding, int copies) {
		super(cellsPerLine, sheetLength, topMargin, bottomMargin, leftMargin, rightMargin, interpoint, copies);
		headerSource = firmware.getHeader(cellsPerLine, sheetLength, interpoint, zfolding);
	}
	@Override
	protected ByteSource getHeader() {
		return headerSource;
	}
}