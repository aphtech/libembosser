package org.brailleblaster.libembosser.utils;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class BrailleMapperTest {
	@DataProvider(name="basicFastMapperProvider")
	public Iterator<Object[]> basicFastMapperProvider() {
		List<Object[]> data = Lists.newArrayList();
		StringBuilder sb = new StringBuilder(256);
		for (int i = 0; i < 256; i++) {
			sb.append((char)i);
		}
		data.add(new Object[] {BrailleMapper.ASCII_TO_UNICODE, BrailleMapper.ASCII_TO_UNICODE_FAST, sb.toString()});
		// ASCII should remain as ASCII when doing unicode to ASCII
		sb = new StringBuilder(64);
		for (int i = 0x20; i < 0x30; i++) {
			sb.append((char)i);
		}
		data.add(new Object[] {BrailleMapper.UNICODE_TO_ASCII, BrailleMapper.UNICODE_TO_ASCII_FAST, sb.toString()});
		// Both should change lowercase ASCII Braille to uppercase ASCII Braille
		sb = new StringBuilder(32);
		for (int i = 96; i < 128; i++) {
			sb.append((char)i);
		}
		data.add(new Object[] { BrailleMapper.UNICODE_TO_ASCII, BrailleMapper.UNICODE_TO_ASCII_FAST, sb.toString()});
		// Check that unicode Braille passes through ASCII to unicode without being changed
		sb = new StringBuilder(256);
		for (int i = 0x2800; i < 0x2900; i++) {
			sb.append((char)i);
		}
		data.add(new Object[] {BrailleMapper.ASCII_TO_UNICODE, BrailleMapper.ASCII_TO_UNICODE_FAST, sb.toString()});
		// The 6-dot unicode Braille characters should map to ASCII
		sb = new StringBuilder(64);
		for (int i = 0x2800; i < 0x2840; i++) {
			sb.append((char)i);
		}
		data.add(new Object[] {BrailleMapper.UNICODE_TO_ASCII, BrailleMapper.UNICODE_TO_ASCII_FAST, sb.toString()});
		return data.iterator();
	}
	@Test(dataProvider="basicFastMapperProvider")
	public void testBasicAndFastImplementations(BrailleMapper basic, BrailleMapper fast, String testInput) {
		String basicResult = basic.map(testInput);
		String fastResult = fast.map(testInput);
		assertEquals(basicResult.length(), fastResult.length());
		for (int i = 0; i < basicResult.length(); i++) {
			assertEquals(basicResult.charAt(i), fastResult.charAt(i));
		}
	}
}
