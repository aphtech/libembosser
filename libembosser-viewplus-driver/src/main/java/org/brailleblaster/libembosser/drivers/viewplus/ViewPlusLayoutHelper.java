/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.viewplus;

import static com.google.common.base.Preconditions.checkArgument;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.Map;

import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.InterpointLayoutHelper;
import org.brailleblaster.libembosser.spi.BrlCell;

import com.google.common.collect.ImmutableMap;

public class ViewPlusLayoutHelper implements InterpointLayoutHelper {
	private final static int[] CELL_OFFSETS = new int[] {1, 0, 1, 2, 0};

	private final Map<TextAttribute, Object> brailleAttributes;

	public ViewPlusLayoutHelper() {
		brailleAttributes = ImmutableMap.of(TextAttribute.FAMILY, "Braille29", TextAttribute.SIZE, 29, TextAttribute.FOREGROUND, new Color(6, 7, 8));
	}

	@Override
	public Map<TextAttribute, Object> getBrailleAttributes(BrlCell brailleCell) {
		return brailleAttributes;
	}
	@Override
	public int getLineSpacing() {
		return 0;
	}

	@Override
	public double calculateMargin(double desiredWidth) {
		checkArgument(desiredWidth >= 0, "Desired width cannot be negative.");
		return convertToWholeDots(desiredWidth) * 3.6;
	}

	private int convertToWholeDots(double desiredWidth) {
		return (int)Math.ceil(desiredWidth/3.6);
	}

	@Override
	public double calculateBackMargin(double desiredWidth, double frontMargin, double pageWidth) {
		final int desiredDots = convertToWholeDots(desiredWidth);
		int marginOffset = (convertToWholeDots(pageWidth) - convertToWholeDots(frontMargin) - desiredDots) % 5;
		int cellOffset = CELL_OFFSETS[marginOffset];
		return (desiredDots + cellOffset) * 3.6;
	}

}
