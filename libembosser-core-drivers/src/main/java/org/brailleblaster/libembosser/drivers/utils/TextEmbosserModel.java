/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils;

import com.google.common.collect.ImmutableList;

/**
 * Definition of a text embosser model.
 * 
 * An interface for defining text embosser models by providing the configuration of a text embosser. This contains data such as the line ending convention, the page ending convention as well as any footer.
 * @author Michael Whapples
 *
 */
public interface TextEmbosserModel {
	/**
	 * The bytes \r\n
	 */
	ImmutableList<Byte> RN = ImmutableList.of((byte)'\r', (byte)'\n');
	/**
	 * The bytes \r\n\f
	 */
	ImmutableList<Byte> RNF = ImmutableList.of((byte)'\r', (byte)'\n', (byte)'\f');
	/**
	 * The byte 0x1a
	 */
	ImmutableList<Byte> X1A = ImmutableList.of((byte)0x1a);
	/**
	 * The byte \n
	 */
	ImmutableList<Byte> N = ImmutableList.of((byte)'\n');
	/**
	 * The bytes \n\f
	 */
	ImmutableList<Byte> NF = ImmutableList.of((byte)'\n', (byte)'\f');
	/**
	 * Get the line ending convention.
	 * 
	 * @return The bytes to terminate a line.
	 */
	byte[] getLineEnd();
	/**
	 * Get the page ending convention.
	 * 
	 * @return The bytes to terminate a page.
	 */
	byte[] getPageEnd();
	/**
	 * Get the footer for a document.
	 * 
	 * @return The bytes of the footer.
	 */
	byte[] getDocEnd();
	/**
	 * Get the ID of the embosser model.
	 * 
	 * @return The ID for the embosser model.
	 */
	String getId();
	/**
	 * Get the name of the embosser model.
	 * 
	 * @return The name of the embosser model.
	 */
	String getName();
	/**
	 * Get the manufacturer of the embosser model.
	 * 
	 * @return The manufacturer of the embosser model.
	 */
	String getManufacturer();
	/**
	 * Get the maximum cells the embosser can put on a line.
	 * 
	 * Many embossers have a limit of how many cells they can emboss on a single line, it may be less than can fit on the maximum paper width.
	 * @return The maximum cells which can be embossed per line.
	 */
	int getMaxCellsPerLine();
}
