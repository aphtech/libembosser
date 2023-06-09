/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils;

import static com.google.common.base.Preconditions.checkArgument;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Map;

import org.brailleblaster.libembosser.drivers.generic.GenericGraphicsEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.LayoutHelper;
import org.brailleblaster.libembosser.spi.BrlCell;

import com.google.common.collect.ImmutableMap;

public class DefaultLayoutHelper implements LayoutHelper {
	private final Map<TextAttribute, Object> brailleAttributes;

	public DefaultLayoutHelper() {
		Font baseFont;
		try {
			baseFont = Font.createFont(Font.TRUETYPE_FONT, GenericGraphicsEmbosser.class.getResourceAsStream("/org/brailleblaster/libembosser/drivers/fonts/APH_Braille_Font-6.otf"));
		} catch (FontFormatException | IOException e) {
			throw new RuntimeException("Problem creating font, should not occur", e);
		}
		Font font = baseFont.deriveFont(24.0f);
		brailleAttributes = ImmutableMap.of(TextAttribute.FONT, font);
	}

	@Override
	public Map<TextAttribute, Object> getBrailleAttributes(BrlCell brailleCell) {
		return brailleAttributes;
	}
	@Override
	public int getLineSpacing() {
		return 3;
	}

	@Override
	public double calculateMargin(double desiredWidth) {
		checkArgument(desiredWidth >= 0, "Desired width must not be negative.");
		// We assume the embosser can place dots at any position.
		// Therefore return the desired margin.
		return desiredWidth;
	}

}
