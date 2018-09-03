package org.brailleblaster.libembosser.spi;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
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
			assertTrue(a.hashCode() == b.hashCode());
		assertEquals(b.equals(c), expectedBC);
		assertEquals(c.equals(b), expectedBC);
		if (expectedBC) 
			assertTrue(b.hashCode() == c.hashCode());
		assertEquals(a.equals(c), expectedAB && expectedBC);
		assertEquals(c.equals(a), expectedAB && expectedBC);
	}
}
