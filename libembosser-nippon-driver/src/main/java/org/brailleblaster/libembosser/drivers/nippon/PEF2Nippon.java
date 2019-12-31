package org.brailleblaster.libembosser.drivers.nippon;

import org.brailleblaster.libembosser.utils.BrailleMapper;
import org.w3c.dom.Element;

/**
 * Convert PEF to the Nippon embosser format. 
 */
public class PEF2Nippon {
	/**
	 * Map a row element to ASCII Braille.
	 * 
	 * @param The row element.
	 * @return The ASCII Braille string representing the row content.
	 */
	String rowToAscii(Element row) {
		String brlUnicode = row.getTextContent();
		return brlUnicode.chars().filter(c -> c >= 0x2800 && c <= 0x28ff).map(c -> BrailleMapper.UNICODE_TO_ASCII_FAST.map((char)c)).collect(() -> new StringBuilder(), (a, c) -> a.append((char)c), (s1, s2) -> s1.append(s2)).toString();
	}
}
