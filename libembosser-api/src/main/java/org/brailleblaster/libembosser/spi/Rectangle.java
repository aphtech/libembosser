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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rectangle other = (Rectangle) obj;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}
}
