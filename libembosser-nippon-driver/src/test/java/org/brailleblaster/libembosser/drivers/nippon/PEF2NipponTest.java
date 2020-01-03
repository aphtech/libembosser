package org.brailleblaster.libembosser.drivers.nippon;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
}
