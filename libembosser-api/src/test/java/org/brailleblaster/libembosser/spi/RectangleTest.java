/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class RectangleTest {
	@DataProvider(name="rectangleEqualsProvider")
	public Iterator<Object[]> rectangleEqualsProvider() {
		List<Object[]> data = Lists.newArrayList();
		data.add(new Object[] {new Rectangle("10.0", "10.1"), new Rectangle("10", "10.1"), new Rectangle("10.0", "10.10"), true, true});
		data.add(new Object[] {new Rectangle("24.5", "24.5"), new Rectangle("24.50", "24.50"), new Rectangle("24.5", "31.1"), true, false});
		return data.iterator();
	}
	@Test(dataProvider="rectangleEqualsProvider")
	public void testEquals(Rectangle a, Rectangle b, Rectangle c, boolean expectedAB, boolean expectedBC) {
		assertEquals(a.equals(b), expectedAB);
		assertEquals(b.equals(a), expectedAB);
		if (expectedAB)
            assertEquals(b.hashCode(), a.hashCode());
		assertEquals(b.equals(c), expectedBC);
		assertEquals(c.equals(b), expectedBC);
		if (expectedBC)
			assertEquals(c.hashCode(), b.hashCode());
		assertEquals(a.equals(c), expectedAB && expectedBC);
		assertEquals(c.equals(a), expectedAB && expectedBC);
	}
}
