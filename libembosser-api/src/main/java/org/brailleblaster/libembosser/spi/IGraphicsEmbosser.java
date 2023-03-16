/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

/**
 * Prefer GraphicsEmbosser interface.
 * @author Michael Whapples
 *
 */
@Deprecated
public interface IGraphicsEmbosser extends Embosser {
	/**
	 * Get the maximum resolution supported by this embosser.
	 * 
	 * @return The resolution in DPI.
	 */
    int getResolution();
}
