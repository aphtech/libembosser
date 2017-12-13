package org.brailleblaster.libembosser.spi;

import java.math.BigDecimal;

public final class Margins {
	public final static Margins NO_MARGINS = new Margins(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
	private final BigDecimal left, right, top, bottom;
	public Margins(BigDecimal left, BigDecimal right, BigDecimal top, BigDecimal bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	public BigDecimal getLeft() {
		return left;
	}
	public BigDecimal getRight() {
		return right;
	}
	public BigDecimal getTop() {
		return top;
	}
	public BigDecimal getBottom() {
		return bottom;
	}
}
