package org.brailleblaster.libembosser.spi;

import java.math.BigDecimal;

/**
 * A rectangular area.
 * 
 * @author Michael Whapples
 *
 */
public final class Rectangle {
	private final BigDecimal width;
	private final BigDecimal height;
	public Rectangle(BigDecimal width, BigDecimal height) {
		if (width == null || height == null) {
			throw new NullPointerException("Neither width or height can be null");
		}
		this.width = width;
		this.height = height;
	}
	public BigDecimal getWidth() {
		return width;
	}
	public BigDecimal getHeight() {
		return height;
	}
}
