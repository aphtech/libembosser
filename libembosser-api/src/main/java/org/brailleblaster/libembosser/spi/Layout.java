/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

public enum Layout {
	INTERPOINT(true), P1ONLY(false), P2ONLY(false),
	SADDLE_STITCH_SINGLE_SIDED(false), SADDLE_STITCH_DOUBLE_SIDED(true),
	Z_FOLDING_SINGLE_HORIZONTAL(false), Z_FOLDING_DOUBLE_HORIZONTAL(true),
	Z_FOLDING_SINGLE_VERTICAL(false), Z_FOLDING_DOUBLE_VERTICAL(true);
	private boolean doubleSide;
	Layout(boolean doubleSide) {
		this.doubleSide = doubleSide;
	}
	public boolean isDoubleSide() {
		return doubleSide;
	}
}
