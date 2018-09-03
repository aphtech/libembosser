package org.brailleblaster.libembosser.spi;

import static org.testng.Assert.assertEquals;
import java.math.BigDecimal;

import org.testng.annotations.Test;

public class RectangleTest {
	@Test
	public void testEquals() {
		Rectangle a = new Rectangle(new BigDecimal("10.0"), new BigDecimal("10.1"));
		Rectangle b = new Rectangle(new BigDecimal("10"), new BigDecimal("10.1"));
		Rectangle c = new Rectangle(new BigDecimal("10.00"), new BigDecimal("10.10"));
		boolean expectedAB = true;
		boolean expectedBC = true;
		assertEquals(a.equals(b), expectedAB);
		assertEquals(b.equals(a), expectedAB);
		assertEquals(a.hashCode() == b.hashCode(), expectedAB);
		assertEquals(b.equals(c), expectedBC);
		assertEquals(c.equals(b), expectedBC);
		assertEquals(b.hashCode() == c.hashCode(), expectedBC);
		assertEquals(a.equals(c), expectedAB && expectedBC);
		assertEquals(c.equals(a), expectedAB && expectedBC);
	}
}
