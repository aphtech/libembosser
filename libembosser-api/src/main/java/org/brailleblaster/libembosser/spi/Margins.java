/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bottom == null) ? 0 : bottom.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((top == null) ? 0 : top.hashCode());
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
		Margins other = (Margins) obj;
		if (bottom == null) {
			if (other.bottom != null)
				return false;
		} else if (!bottom.equals(other.bottom))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (top == null) {
			return other.top == null;
		} else return top.equals(other.top);
	}
}
