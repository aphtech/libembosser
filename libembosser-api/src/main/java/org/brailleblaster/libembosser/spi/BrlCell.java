/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

import java.math.BigDecimal;

/**
 * Standard Braille cell types.
 * 
 * @author Michael Whapples
 *
 */
public enum BrlCell {
	NLS(new BigDecimal("6.20"), new BigDecimal("10.00"), 6),
	CALIFORNIA_SIGN(new BigDecimal("7.60"), new BigDecimal("10.00"), 6),
	JUMBO(new BigDecimal("8.76"), new BigDecimal("12.70"), 6),
	ENHANCED_LINE_SPACING(new BigDecimal("6.10"), new BigDecimal("15.24"), 6),
	SMALL_ENGLISH(new BigDecimal("5.38"), new BigDecimal("8.46"), 6),
	MARBURG_MEDIUM(new BigDecimal("6.00"), new BigDecimal("10.00"), 6),
	UK(new BigDecimal("6.00"), new BigDecimal("10.00"), 6),
	AUSTRALIAN(new BigDecimal("6.00"), new BigDecimal("10.00"), 6);
	private final BigDecimal width;
    private final BigDecimal height;
	private final int dots;
	BrlCell(BigDecimal width, BigDecimal height, int dots) {
		this.width = width;
		this.height = height;
		this.dots = dots;
	}
	/**
	 * Get the width of the Braille cell in mm.
	 * 
	 * The width is defined as the distance from the centre of a dot to the centre of the corresponding dot in the next Braille cell.
	 * 
	 * @return The width of the Braille cell.
	 */
	public BigDecimal getWidth() {
		return width;
	}
	/**
	 * Get the height of the Braille cell in mm.
	 * 
	 * The height is defined as the distance from the centre of a dot to the centre of the corresponding dot in the cell below on the next line, assuming standard line spacing.
	 * 
	 * @return The height of a cell.
	 */
	public BigDecimal getHeight() {
		return height;
	}
	/**
	 * Get the number of dots which make up this cell type.
	 * 
	 * @return The number of dots in this cell type.
	 */
	public int getDots() {
		return dots;
	}
	/**
	 * Get the number of whole cells which fit within a certain width. If this method is passed a negative value it will return 0.
	 * 
	 * @param width The width in mm.
	 * @return The number of whole cells which fit within the width.
	 */
	public int getCellsForWidth(BigDecimal width) {
		if (BigDecimal.ZERO.compareTo(width) > 0) {
			return 0;
		}
		return width.divideToIntegralValue(this.getWidth()).intValue();
	}
	/**
	 * Get the number of whole lines which fit within the height.
	 * 
	 * @param height The height in mm.
	 * @return The number of whole lines which fit within the height.
	 */
	public int getLinesForHeight(BigDecimal height) {
		return height.divideToIntegralValue(this.getHeight()).intValue();
	}
	/**
	 * Get the width required for a number of cells.
	 * 
	 * @param cells The number of cells on a single line.
	 * @return The width in mm.
	 */
	public BigDecimal getWidthForCells(int cells) {
		return width.multiply(BigDecimal.valueOf(cells));
	}
	/**
	 * Get the height required for a number of lines.
	 * 
	 * @param lines The number of lines.
	 * @return The height in mm.
	 */
	public BigDecimal getHeightForLines(int lines) {
		return height.multiply(BigDecimal.valueOf(lines));
	}
}
