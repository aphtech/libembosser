package org.brailleblaster.libembosser.drivers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.brailleblaster.libembosser.drivers.utils.BrailleFilterInputStream;
import org.testng.annotations.Test;

public class BrailleFilterInputStreamTest {
	@Test
	public void testReadByteConversion() {
		byte[] convertArray = new byte[256];
		for (int i = 0; i < 0x60; i++) {
			convertArray[i] = (byte)i;
		}
		for (int i = 0x60; i < 0x80; i++) {
			convertArray[i] = (byte)(i - 0x20);
		}
		for (int i = 0x80; i < 256; i++) {
			convertArray[i] = (byte)i;
		}
		byte[] input = new byte[100000];
		new Random().nextBytes(input);
		InputStream is = new BrailleFilterInputStream(new ByteArrayInputStream(input));
		int counter = 0;
		try {
			int value = is.read();
			while (value >= 0) {
				assertEquals((byte)value, convertArray[Byte.toUnsignedInt(input[counter])]);
				counter++;
				value = is.read();
			}
		} catch (IOException e) {
			fail("Unexpected exception in test", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// Should never happen
			}
		}
		assertEquals(counter, input.length);
	}
}
