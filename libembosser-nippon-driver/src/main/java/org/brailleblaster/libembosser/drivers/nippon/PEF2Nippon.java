package org.brailleblaster.libembosser.drivers.nippon;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.brailleblaster.libembosser.utils.BrailleMapper;
import org.brailleblaster.libembosser.utils.PEFElementType;
import org.brailleblaster.libembosser.utils.PefUtils;
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
	private static class DuplexPagesJoiner {
		private StringBuilder sb = new StringBuilder();
		private int pages = 0;
		public DuplexPagesJoiner append(CharSequence cs) {
			sb.append(cs).append("\f");
			pages++;
			return this;
		}
		public DuplexPagesJoiner append(DuplexPagesJoiner dpj) {
			sb.append(dpj.sb).append("\f");
			pages += dpj.pages;
			return this;
		}
		public String getSection() {
			if (sb.length() == 0) {
				return "";
			} else if (pages % 2 != 0) {
				return sb.append("\u0002\u0001\u0000").toString();
			} else {
				return sb.substring(0, sb.length() - 1);
			}
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
	Collector<CharSequence, ?, String> rowsJoiner() {
		return Collector.of(RowsJoiner::new, RowsJoiner::append, RowsJoiner::append, RowsJoiner::getPageString, new Collector.Characteristics[] {});
	}
	/**
	 * Join pages in a section.
	 * 
	 * This joiner should be used for embossing jobs in single side mode.
	 * 
	 * @return A collector for joining pages together.
	 */
	Collector<CharSequence, ?, String> pagesJoiner() {
		return Collectors.joining("\f");
	}
	/**
	 * Collect pages in a duplex embossing job.
	 * 
	 * This should be used for jobs where the embosser has been set to duplex mode. Use the duplex parameter to indicate whether this specific section is duplex or not.
	 * 
	 * @param duplex Whether this section is duplex.
	 * @return A collector for joining the pages together.
	 */
	Collector<CharSequence, ?, String> duplexPagesJoiner(boolean duplex) {
		return duplex? Collector.of(DuplexPagesJoiner::new, DuplexPagesJoiner::append, DuplexPagesJoiner::append, DuplexPagesJoiner::getSection, new Collector.Characteristics[] {}): Collectors.mapping(p -> new StringBuilder().append(p).append("\f\u0002\u0001\u0000"), pagesJoiner());
	}
	String pageToString(Element page) {
		return PefUtils.findMatchingDescendants(page, PEFElementType.ROW).map(r -> addRowStartAndEnd(rowToAscii(r))).collect(rowsJoiner());
	}
	String sectionToString(Element section) {
		return PefUtils.findMatchingDescendants(section, PEFElementType.PAGE).map(this::pageToString).collect(pagesJoiner());
	}
	String volumeToString(Element volume) {
		return PefUtils.findMatchingDescendants(volume, PEFElementType.SECTION).map(this::sectionToString).collect(pagesJoiner());
	}
	String bodyToString(Element body) {
		return PefUtils.findMatchingDescendants(body, PEFElementType.VOLUME).map(this::volumeToString).collect(pagesJoiner());
	}
}