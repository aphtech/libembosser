/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.viewplus;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;
import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.InterpointLayoutHelper;
import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.LayoutHelper;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Iterators;

public class ViewPlusLayoutHelperTest {
	private final Random random = new Random(System.currentTimeMillis());
	@Test
	public void testGetBrailleAttributes() {
		Map<TextAttribute, Object> actualAttrs = new ViewPlusLayoutHelper().getBrailleAttributes(BrlCell.NLS);
		assertEquals(actualAttrs.size(), 3);
		assertTrue(actualAttrs.containsKey(TextAttribute.FAMILY));
		assertTrue(actualAttrs.containsKey(TextAttribute.SIZE));
		assertTrue(actualAttrs.containsKey(TextAttribute.FOREGROUND));
		assertEquals(actualAttrs.get(TextAttribute.FAMILY), "Braille29");
		assertEquals(actualAttrs.get(TextAttribute.SIZE), 29, "Font size should be 29");
		assertEquals(actualAttrs.get(TextAttribute.FOREGROUND), new Color(6, 7, 8), "Foreground should be Tiger magic colour");
	}
	@DataProvider(name="marginProvider")
	public Iterator<Object[]> marginProvider() {
		return Iterators.forArray(new Object[][] {{3.6, 3.6}, {2.4, 3.6}, {4.1, 7.2}, {0.0, 0.0}});
	}
	@Test(dataProvider="marginProvider")
	public void testCalculateMargin(double margin, double expected) {
		double actual = new ViewPlusLayoutHelper().calculateMargin(margin);
		assertEquals(actual, expected, 0.01);
	}
	@DataProvider(name="negativeNumberProvider")
	public Iterator<Object[]> negativeNumberProvider() {
		return random.doubles(100, Double.NEGATIVE_INFINITY, 0.0).mapToObj(v -> new Object[] {v}).iterator();
	}
	@Test(dataProvider="negativeNumberProvider")
	public void testCalculateMarginRejectNegativeMargin(double margin) {
		final LayoutHelper layoutHelper = new ViewPlusLayoutHelper();
		expectThrows(IllegalArgumentException.class, () -> layoutHelper.calculateMargin(margin));
	}
	@DataProvider(name="backMarginProvider")
	public Object[][] backMarginProvider() {
		return new Object[][] {
			{36.0, 36.0, 720.0, 39.6},
			{18.0, 36.0, 720.0, 21.6},
			{36.0, 18.0, 720.0, 39.6},
			{14.4, 36.0, 720.0, 14.4},
			{21.6, 36.0, 720.0, 21.6},
			{39.6, 36.0, 720.0, 39.6}
		};
	}
	@Test(dataProvider="backMarginProvider")
	public void testCalculateBackMargin(double margin, double frontMargin, double pageWidth, double expected) {
		InterpointLayoutHelper	layoutHelper = new ViewPlusLayoutHelper();
		double actual = layoutHelper.calculateBackMargin(margin, frontMargin, pageWidth);
		assertEquals(actual, expected, 0.01);
	}
}
