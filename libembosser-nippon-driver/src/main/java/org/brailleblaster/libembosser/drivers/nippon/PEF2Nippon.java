package org.brailleblaster.libembosser.drivers.nippon;

import java.util.stream.Collector;
import org.brailleblaster.libembosser.utils.BrailleMapper;
import org.w3c.dom.Element;

/**
 * Convert PEF to the Nippon embosser format. 
 */
public class PEF2Nippon {
	private static class RowsJoiner {
		private StringBuilder sb = new StringBuilder();
		private int rows = 0;
		public RowsJoiner append(CharSequence cs) {
			sb.append(cs);
			rows++;
			return this;
		}
		public RowsJoiner append(RowsJoiner rj) {
			sb.append(rj.sb);
			rows += rj.rows;
			return this;
		}
		public String getPageString() {
			return sb.insert(0, new char[] { '\u0002', '\u0001', (char)rows}).toString();
		}
	}
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
	/**
	 * Add to a row the start and end required for a Nippon embosser.
	 * 
	 * The Nippon embosser requires a line to start with a byte representing the number of bytes for the line (including the \r\n line ending). It also requires that the line is terminated with a \r\n line ending. This method will add these required items to the line of Braille.
	 * 
	 * @param row A line of Braille in ASCII Braille. It is assumed that the string only contains the ASCII Braille for the line of Braille, no checking is performed.
	 * @return The line of Braille with the start and end sequences added.
	 */
	String addRowStartAndEnd(String row) {
		
		return new StringBuilder(row.length() + 3).append((char)(row.length() + 2)).append(row).append("\r\n").toString();
	}
	/**
	 * Join the rows into the page data.
	 * 
	 * The Nippon format requires the page to start with a byte sequence which declares the number of lines on the page. This joiner as well as joining the rows will add this required prefix. No form feed is added at the end of the page as this is not always needed (eg. on the last page).
	 * 
	 * @return A collector for joining the rows into a page.
	 */
	public Collector<CharSequence, ?, String> rowsJoiner() {
		return Collector.of(RowsJoiner::new, RowsJoiner::append, RowsJoiner::append, RowsJoiner::getPageString, new Collector.Characteristics[] {});
	}
}
