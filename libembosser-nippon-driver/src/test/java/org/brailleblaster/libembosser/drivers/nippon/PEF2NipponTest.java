package org.brailleblaster.libembosser.drivers.nippon;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

public class PEF2NipponTest {
	@DataProvider(name="rowProvider")
	public Object[][] rowProvider() {
		return new Object[][] {
			{ "<row>&#x2801;&#x2800;&#x2803;</row>", "A B" },
			{ "<row>&#x2803;&#x2801;&#x2809;&#x2805;</row>", "BACK" },
			{ "<row>   &#x2801;&#x2800;&#x2803;</row>", "A B" },
			{ "<row> &#x2803;&#x2801;&#x2809;&#x2805;    </row>", "BACK" },
			{ "<row>\n&#x2801;&#x2800;&#x2803;\n</row>", "A B" },
			{ "<row>\n  &#x2803;&#x2801;&#x2809;&#x2805;</row>", "BACK" },
			{ "<row>&#x2801;&#x2800;&#x2803;&#x2800;  </row>", "A B " },
			{ "<row>   &#x2800;&#x2800;&#x2803;&#x2801;&#x2809;&#x2805;</row>", "  BACK" },
			{ "<row>&#x2801;\n  &#x2800;&#x2803;</row>", "A B" },
			{ "<row>&#x2803;&#x2801;    &#x2809;&#x2805;</row>", "BACK" },
		};
	}
	@Test(dataProvider="rowProvider")
	public void testRowToAsciiBraille(String inputXml, String expected) {
		Element row = null;
		try (InputStream is = new ByteArrayInputStream(inputXml.getBytes(Charsets.UTF_8))) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			row = db.parse(is).getDocumentElement();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			fail("Problem parsing the XML", e);
		}
		String actual = new PEF2Nippon().rowToAscii(row);
		assertEquals(actual, expected);
	}
	@DataProvider(name="rowStringsProvider")
	public Object[][] rowStringsProvider() {
		return new Object[][] {
			{ "BACK", "\u0006BACK\r\n" },
			{ "B A", "\u0005B A\r\n" },
			{ "", "\u0002\r\n" },
			{ " ", "\u0003 \r\n" },
			{ Strings.repeat("AB", 20), "\u002a" + Strings.repeat("AB", 20) + "\r\n" },
		};
	}
	@Test(dataProvider="rowStringsProvider")
	public void testAddRowStartAndEnd(String row, String expected) {
		String actual = new PEF2Nippon().addRowStartAndEnd(row);
		assertEquals(actual, expected);
	}
	@DataProvider(name="rowsJoinerProvider")
	public Object[][] rowsJoinerProvider() {
		return new Object[][] {
			{ Stream.<String>empty(), "\u0002\u0001\u0000" },
			{ Stream.of("\u0003 \r\n", "\u0005A B\r\n"), "\u0002\u0001\u0002\u0003 \r\n\u0005A B\r\n" },
			{ Stream.of("\u0006BACK\r\n", "\u0005GOT\r\n", "\u0013BACKW>DS TGR ON A\r\n", "\u0003 \r\n"), "\u0002\u0001\u0004\u0006BACK\r\n\u0005GOT\r\n\u0013BACKW>DS TGR ON A\r\n\u0003 \r\n" },
		};
	}
	@Test(dataProvider="rowsJoinerProvider")
	public void testRowsJoiner(Stream<String> rows, String expected) {
		String actual = rows.collect(new PEF2Nippon().rowsJoiner());
		assertEquals(actual, expected);
	}
	@DataProvider(name="pagesJoinerProvider")
	public Object[][] pagesJoinerProvider() {
		return new Object[][] {
			{ Stream.<String>empty(), "" },
			{ Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n"), "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n" },
			{ Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n", "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n"), "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\f\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n" },
		};
	}
	@Test(dataProvider="pagesJoinerProvider")
	public void testPagesJoinerProvider(Stream<String> pagesStream, String expected) {
		Collector<CharSequence, ?, String> pagesJoiner = new PEF2Nippon().pagesJoiner();
		String actual = pagesStream.collect(pagesJoiner);
		assertEquals(actual, expected);
	}
	@DataProvider(name="duplexPagesJoinerProvider")
	public Object[][] duplexPagesJoinerProvider() {
		return new Object[][] {
			{ Stream.<String>empty(), false, "" },
			{ Stream.<String>empty(), true, "" },
			{ Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n"), false, "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\f\u0002\u0001\u0000" },
			{ Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n"), true, "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\f\u0002\u0001\u0000" },
			{ Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n", "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n"), false, "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\f\u0002\u0001\u0000\f\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n\f\u0002\u0001\u0000" },
			{ Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n", "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n"), true, "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\f\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n" },
			{ Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n", "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n", "\u0002\u0001\u0003\u0006BACK\r\n\u0007FINDS\r\n\u0004CD\r\n"), false, "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\f\u0002\u0001\u0000\f\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n\f\u0002\u0001\u0000\f\u0002\u0001\u0003\u0006BACK\r\n\u0007FINDS\r\n\u0004CD\r\n\f\u0002\u0001\u0000" },
			{ Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n", "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n", "\u0002\u0001\u0003\u0006BACK\r\n\u0007FINDS\r\n\u0004CD\r\n"), true, "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\f\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n\f\u0002\u0001\u0003\u0006BACK\r\n\u0007FINDS\r\n\u0004CD\r\n\f\u0002\u0001\u0000" },
		};
	}
	@Test(dataProvider="duplexPagesJoinerProvider")
	public void testDuplexPagesJoinerProvider(Stream<String> pagesStream, boolean duplex, String expected) {
		Collector<CharSequence, ?, String> pagesJoiner = new PEF2Nippon().duplexPagesJoiner(duplex);
		String actual = pagesStream.collect(pagesJoiner);
		assertEquals(actual, expected);
	}
	@DataProvider(name="elementToStringProvider")
	public Object[][] elementToStringProvider() {
		final PEF2Nippon pefToNippon = new PEF2Nippon();
		Function<Element, String> pageToString = pefToNippon::pageToString;
		Function<Element, String> sectionToString = pefToNippon::sectionToString;
		Function<Element, String> volumeToString = pefToNippon::volumeToString;
		Function<Element, String> bodyToString = pefToNippon::bodyToString;
		Function<Element, String> pefToString = pefToNippon::pefToString;
		return new Object[][] {
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"/>", "\u0002\u0001\u0000", pageToString},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;&#x2803;</row></page>", "\u0002\u0001\u0001\u0004AB\r\n", pageToString},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2803;&#x2801;</row><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page>", "\u0002\u0001\u0002\u0004BA\r\n\u0007A B C\r\n", pageToString},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page/></section>", "\u0002\u0001\u0000", sectionToString},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page/><page/></section>", "\u0002\u0001\u0000\f\u0002\u0001\u0000", sectionToString},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2809;&#x2801;&#x2803;</row></page></section>", "\u0002\u0001\u0001\u0005CAB\r\n", sectionToString},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2809;&#x2801;&#x2803;</row><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section>", "\u0002\u0001\u0002\u0005CAB\r\n\u0007A B C\r\n", sectionToString},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2809;&#x2801;&#x2803;</row><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row><row>&#x2801;&#x2803;</row></page></section>", "\u0002\u0001\u0002\u0005CAB\r\n\u0007A B C\r\n\f\u0002\u0001\u0002\u0007A B C\r\n\u0004AB\r\n", sectionToString},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page/></section></volume>", "\u0002\u0001\u0000", volumeToString},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page/><page/></section></volume>", "\u0002\u0001\u0000\f\u0002\u0001\u0000", volumeToString},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page/></section><section><page/></section></volume>", "\u0002\u0001\u0000\f\u0002\u0001\u0000", volumeToString},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume>", "\u0002\u0001\u0002\u0005GOT\r\n\u0005ABC\r\n", volumeToString},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume>", "\u0002\u0001\u0001\u0007A B C\r\n\f\u0002\u0001\u0001\u0005ABC\r\n", volumeToString},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume>", "\u0002\u0001\u0001\u0007A B C\r\n\f\u0002\u0001\u0001\u0005ABC\r\n", volumeToString},
			{"<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page/></section></volume></body>", "\u0001\u0000\u0000\u0002\u0001\u0000\u0003", bodyToString},
			{"<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page/><page/></section></volume></body>", "\u0001\u0000\u0000\u0002\u0001\u0000\f\u0002\u0001\u0000\u0003", bodyToString},
			{"<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page/></section><section><page/></section></volume></body>", "\u0001\u0000\u0000\u0002\u0001\u0000\f\u0002\u0001\u0000\u0003", bodyToString},
			{"<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body>", "\u0001\u0000\u0000\u0002\u0001\u0002\u0005GOT\r\n\u0005ABC\r\n\u0003", bodyToString},
			{"<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body>", "\u0001\u0000\u0000\u0002\u0001\u0001\u0007A B C\r\n\f\u0002\u0001\u0001\u0005ABC\r\n\u0003", bodyToString},
			{"<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body>", "\u0001\u0000\u0000\u0002\u0001\u0001\u0007A B C\r\n\f\u0002\u0001\u0001\u0005ABC\r\n\u0003", bodyToString},
			{"<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2800;&#x2803;</row></page><page><row>&#x2813;&#x2800;&#x281b;&#x2815;&#x281e;</row></page></section></volume><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body>", "\u0001\u0000\u0000\u0002\u0001\u0002\u0005GOT\r\n\u0005A B\r\n\f\u0002\u0001\u0001\u0007H GOT\r\n\f\u0002\u0001\u0001\u0007A B C\r\n\f\u0002\u0001\u0001\u0005ABC\r\n\u0003", bodyToString},
			
			{"<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page/></section></volume></body></pef>", "\u0001\u0000\u0000\u0002\u0001\u0000\u0003", pefToString},
			{"<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page/><page/></section></volume></body></pef>", "\u0001\u0000\u0000\u0002\u0001\u0000\f\u0002\u0001\u0000\u0003", pefToString},
			{"<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page/></section><section><page/></section></volume></body></pef>", "\u0001\u0000\u0000\u0002\u0001\u0000\f\u0002\u0001\u0000\u0003", pefToString},
			{"<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body></pef>", "\u0001\u0000\u0000\u0002\u0001\u0002\u0005GOT\r\n\u0005ABC\r\n\u0003", pefToString},
			{"<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body></pef>", "\u0001\u0000\u0000\u0002\u0001\u0001\u0007A B C\r\n\f\u0002\u0001\u0001\u0005ABC\r\n\u0003", pefToString},
			{"<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body></pef>", "\u0001\u0000\u0000\u0002\u0001\u0001\u0007A B C\r\n\f\u0002\u0001\u0001\u0005ABC\r\n\u0003", pefToString},
			{"<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2800;&#x2803;</row></page><page><row>&#x2813;&#x2800;&#x281b;&#x2815;&#x281e;</row></page></section></volume><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body></pef>", "\u0001\u0000\u0000\u0002\u0001\u0002\u0005GOT\r\n\u0005A B\r\n\f\u0002\u0001\u0001\u0007H GOT\r\n\f\u0002\u0001\u0001\u0007A B C\r\n\f\u0002\u0001\u0001\u0005ABC\r\n\u0003", pefToString},
		};
	}
	@Test(dataProvider="elementToStringProvider")
	public void testElementToStringConversion(String inputXml, String expected, Function<Element, String> conversionFunction) {
		Element page = null;
		try (InputStream is = new ByteArrayInputStream(inputXml.getBytes(Charsets.UTF_8))) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			page = db.parse(is).getDocumentElement();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			fail("Problem parsing the input XML", e);
		}
		String actual = conversionFunction.apply(page);
		assertEquals(actual, expected);
	}
	
}
