package org.brailleblaster.libembosser.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xmlunit.assertj.XmlAssert;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

public class PefUtilsTest {
	@DataProvider(name="brfProvider")
	public Iterator<Object[]> brfProvider() {
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/minimal_40x25.xml", "/org/brailleblaster/libembosser/utils/docs/blank.brf", "MinimalDoc", 40, 25, false});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/minimal_40x25_interpoint.xml", "/org/brailleblaster/libembosser/utils/docs/blank.brf", "MinimalDocInterpoint", 40, 25, true});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/minimal_30x26.xml", "/org/brailleblaster/libembosser/utils/docs/blank.brf", "MinimalDoc", 30, 26, false});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/minimal_30x26_interpoint.xml", "/org/brailleblaster/libembosser/utils/docs/blank.brf", "MinimalDocInterpoint", 30, 26, true});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/single_page_40x25.xml", "/org/brailleblaster/libembosser/utils/docs/single_page_40x25.brf", "SinglePage", 40, 25, false});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/multiple_pages_40x25.xml", "/org/brailleblaster/libembosser/utils/docs/multiple_pages_40x25.brf", "MultiplePages", 40, 25, false});
		return data.iterator();
	}

	@Test(dataProvider="brfProvider")
	public void testFromBrfInputStream(String pefResource, String brfResource, String id, int cells, int lines, boolean duplex) {
		try(InputStream expected = getClass().getResourceAsStream(pefResource); InputStream brf = getClass().getResourceAsStream(brfResource)) {
			Document actual = PefUtils.fromBrf(brf, id, cells, lines, duplex);
			XmlAssert.assertThat(actual).and(expected).normalizeWhitespace().areIdentical();
		} catch (ParserConfigurationException | IOException e) {
			fail("Problem with XML parser");
		}
		
		
	}
	@Test(dataProvider="brfProvider")
	public void testFromBrfReader(String pefResource, String brfResource, String id, int cells, int lines, boolean duplex) {
		CharSource brfSource = Resources.asCharSource(getClass().getResource(brfResource), Charsets.US_ASCII);
		try(Reader brf = brfSource.openBufferedStream(); InputStream expected = getClass().getResourceAsStream(pefResource)) {
			Document actual = PefUtils.fromBrf(brf, id, cells, lines, duplex);
			XmlAssert.assertThat(actual).and(expected).normalizeWhitespace().areIdentical();
		} catch (ParserConfigurationException | IOException e) {
			fail("Problem with XML parser");
		}
		
	}
	@DataProvider(name="pageProvider")
	public Object[][] pageProvider() {
		return new Object[][] {
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"/>", new String[] {}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;&#x2800;&#x2803;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;&#x2800;&#x2803;</row>"}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row><row>&#x2803;&#x2806;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\">   <row>&#x2801;</row>ffed<row>&#x2803;&#x2806;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row><row xmlns=\"some.other.ns\">&#x2805;</row><p xmlns=\"some.other\">&#x2803;</p><row>&#x2803;&#x2806;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><p xmlns=\"another.ns\"><row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row></p><row>&#x2803;&#x2806;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}},
		};
	}
	@Test(dataProvider="pageProvider")
	public void testGetRowsAsStream(String pageXml, String[] rowsXml) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			fail("Problem setting up XML parser", e);
		}
		Element inputPage = null;
		try (InputStream is = new ByteArrayInputStream(pageXml.getBytes(Charsets.UTF_8))) {
			inputPage = db.parse(is).getDocumentElement();
		} catch (IOException | SAXException e) {
			fail("Problem parsing input XML", e);
		}
		ImmutableList.Builder<Element> rowsBuilder = ImmutableList.builderWithExpectedSize(rowsXml.length);
		for (String rowXml: rowsXml) {
			try (InputStream is = new ByteArrayInputStream(rowXml.getBytes(Charsets.UTF_8))) {
				Element rowElem = db.parse(is).getDocumentElement();
				rowsBuilder.add(rowElem);
			} catch (IOException | SAXException e) {
				fail("Problem parsing expected XML", e);
			}
		}
		List<Element> expected = rowsBuilder.build();
		Stream<Element> actualStream = PefUtils.getRowsAsStream(inputPage);
		List<Element> actualAsList = actualStream.collect(ImmutableList.toImmutableList());
		assertEquals(actualAsList.size(), expected.size(), "Not got the expected number of elements in the stream.");
		for (int i = 0; i < actualAsList.size(); i++) {
			XmlAssert.assertThat(actualAsList.get(i)).and(expected.get(i)).areIdentical();
		}
	}
}
