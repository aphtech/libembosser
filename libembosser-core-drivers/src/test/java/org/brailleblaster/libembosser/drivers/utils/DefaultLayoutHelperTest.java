package org.brailleblaster.libembosser.drivers.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.brailleblaster.libembosser.spi.BrlCell;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DefaultLayoutHelperTest {
	@Test
	public void testGetBrailleAttributes() {
		Map<TextAttribute, Object> actualAttrs = new DefaultLayoutHelper().getBrailleAttributes(BrlCell.NLS);
		assertEquals(actualAttrs.size(), 1, "Expected number of text attributes should be 1");
		assertTrue(actualAttrs.containsKey(TextAttribute.FONT));
		Font actualFont = (Font)actualAttrs.get(TextAttribute.FONT);
		assertEquals(actualFont.getName(), "APH Braille Font-6dot");
		assertEquals(actualFont.getSize(), 26);
	}
	@DataProvider(name="marginProvider")
	public Iterator<Object[]> marginprovider() {
		Random random = new Random(System.currentTimeMillis());
		return random.doubles(100, 0.0, Double.POSITIVE_INFINITY).mapToObj(v -> new Object[] { v, v }).iterator();
	}
	@Test(dataProvider="marginProvider")
	public void testCalculateMargin(double margin, double expected) {
		double actual = new DefaultLayoutHelper().calculateMargin(margin);
		assertEquals(actual, expected, 0.01);
	}
	@DataProvider(name="negativeNumberProvider")
	public Iterator<Object[]> negativeNumberProvider() {
		Random random = new Random(System.currentTimeMillis());
		return random.doubles(100, Double.NEGATIVE_INFINITY, 0.0).mapToObj(v -> new Object[] {v}).iterator();
	}
	@Test(dataProvider="negativeNumberProvider")
	public void testCalculateMarginRejectNegativeNumber(double margin) {
		final DefaultLayoutHelper layoutHelper = new DefaultLayoutHelper();
		expectThrows(IllegalArgumentException.class, () -> layoutHelper.calculateMargin(margin));
	}
}
