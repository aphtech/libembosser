package org.brailleblaster.libembosser.drivers.nippon;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;

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
}
