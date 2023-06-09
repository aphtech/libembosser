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
 * A rectangular area.
 * 
 * @author Michael Whapples
 *
 */
public final class Rectangle {
	private final BigDecimal width;
	private final BigDecimal height;
	public Rectangle(String width, String height) {
		this(new BigDecimal(width), new BigDecimal(height));
	}
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
		result = prime * result + ((height == null) ? 0 : height.intValue());
		result = prime * result + ((width == null) ? 0 : width.intValue());
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
		} else if (height.compareTo(other.height) != 0)
			return false;
		if (width == null) {
			return other.width == null;
		} else return width.compareTo(other.width) == 0;
	}
}
