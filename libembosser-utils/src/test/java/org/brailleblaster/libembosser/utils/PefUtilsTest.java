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
	@DataProvider(name="findMatchingDescendantsProvider")
	public Object[][] findMatchingDescendantsProvider() {
		return new Object[][] {
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"/>", new String[] {}, new PEFElementType[] {PEFElementType.ROW}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;&#x2800;&#x2803;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;&#x2800;&#x2803;</row>"}, new PEFElementType[] {PEFElementType.ROW}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row><row>&#x2803;&#x2806;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}, new PEFElementType[] {PEFElementType.ROW}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\">   <row>&#x2801;</row>ffed<row>&#x2803;&#x2806;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}, new PEFElementType[] {PEFElementType.ROW}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row><row xmlns=\"some.other.ns\">&#x2805;</row><p xmlns=\"some.other\">&#x2803;</p><row>&#x2803;&#x2806;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}, new PEFElementType[] {PEFElementType.ROW}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><p xmlns=\"another.ns\"><row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row></p><row>&#x2803;&#x2806;</row></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}, new PEFElementType[] {PEFElementType.ROW}},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"/>", new String[] {}, new PEFElementType[] {PEFElementType.PAGE}},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;&#x2800;&#x2803;</row></page></section>", new String[] {"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;&#x2800;&#x2803;</row></page>"}, new PEFElementType[] {PEFElementType.PAGE}},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;</row></page><page><row>&#x2803;&#x2806;</row></page></section>", new String[] {"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row></page>", "<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2803;&#x2806;</row></page>"}, new PEFElementType[] {PEFElementType.PAGE}},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\">   <page><row>&#x2801;</row></page>ffed<page><row>&#x2803;&#x2806;</row></page></section>", new String[] {"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row></page>", "<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2803;&#x2806;</row></page>"}, new PEFElementType[] {PEFElementType.PAGE}},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;</row></page><row xmlns=\"some.other.ns\">&#x2805;</row><p xmlns=\"some.other\">&#x2803;</p><page><row>&#x2803;&#x2806;</row></page></section>", new String[] {"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row></page>", "<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2803;&#x2806;</row></page>"}, new PEFElementType[] {PEFElementType.PAGE}},
			{"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><p xmlns=\"another.ns\"><page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row></page></p><page><row>&#x2803;&#x2806;</row></page></section>", new String[] {"<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;</row></page>", "<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2803;&#x2806;</row></page>"}, new PEFElementType[] {PEFElementType.PAGE}},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"/>", new String[] {}, new PEFElementType[] {PEFElementType.SECTION}},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x2801;&#x2800;&#x2803;</row></page></section></volume>", new String[] {"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;&#x2800;&#x2803;</row></page></section>"}, new PEFElementType[] {PEFElementType.SECTION}},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x2801;</row></page></section><section><page><row>&#x2803;&#x2806;</row></page></section></volume>", new String[] {"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;</row></page></section>", "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2803;&#x2806;</row></page></section>"}, new PEFElementType[] {PEFElementType.SECTION}},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\">   <section><page><row>&#x2801;</row></page></section>ffed<section><page><row>&#x2803;&#x2806;</row></page></section></volume>", new String[] {"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;</row></page></section>", "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2803;&#x2806;</row></page></section>"}, new PEFElementType[] {PEFElementType.SECTION}},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x2801;</row></page></section><row xmlns=\"some.other.ns\">&#x2805;</row><p xmlns=\"some.other\">&#x2803;</p><section><page><row>&#x2803;&#x2806;</row></page></section></volume>", new String[] {"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;</row></page></section>", "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2803;&#x2806;</row></page></section>"}, new PEFElementType[] {PEFElementType.SECTION}},
			{"<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><p xmlns=\"another.ns\"><section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;</row></page></section></p><section><page><row>&#x2803;&#x2806;</row></page></section></volume>", new String[] {"<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2801;</row></page></section>", "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2803;&#x2806;</row></page></section>"}, new PEFElementType[] {PEFElementType.SECTION}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\" xmlns:tg=\"http://www.aph.org/ns/tactile-graphics/1.0\"><row>&#x2801;</row><tg:graphic><row>&#x2803;&#x2806;</row></tg:graphic></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2803;&#x2806;</row>"}, new PEFElementType[] {PEFElementType.ROW}},
			{"<page xmlns=\"http://www.daisy.org/ns/2008/pef\" xmlns:tg=\"http://www.aph.org/ns/tactile-graphics/1.0\"><row>&#x2801;</row><tg:graphic><row>&#x2803;&#x2806;</row></tg:graphic></page>", new String[] {"<row xmlns=\"http://www.daisy.org/ns/2008/pef\">&#x2801;</row>", "<tg:graphic xmlns=\"http://www.daisy.org/ns/2008/pef\" xmlns:tg=\"http://www.aph.org/ns/tactile-graphics/1.0\"><row>&#x2803;&#x2806;</row></tg:graphic>"}, new PEFElementType[] {PEFElementType.ROW, PEFElementType.GRAPHIC}},
		};
	}
	@Test(dataProvider="findMatchingDescendantsProvider")
	public void testFindMatchingDescendants(String parentXml, String[] descendantsXml, PEFElementType[] elementTypes) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			fail("Problem setting up XML parser", e);
		}
		Element inputParent = null;
		try (InputStream is = new ByteArrayInputStream(parentXml.getBytes(Charsets.UTF_8))) {
			inputParent = db.parse(is).getDocumentElement();
		} catch (IOException | SAXException e) {
			fail("Problem parsing input XML", e);
		}
		ImmutableList.Builder<Element> descendantsBuilder = ImmutableList.builderWithExpectedSize(descendantsXml.length);
		for (String descendantXml: descendantsXml) {
			try (InputStream is = new ByteArrayInputStream(descendantXml.getBytes(Charsets.UTF_8))) {
				Element descendantElem = db.parse(is).getDocumentElement();
				descendantsBuilder.add(descendantElem);
			} catch (IOException | SAXException e) {
				fail("Problem parsing expected XML", e);
			}
		}
		List<Element> expected = descendantsBuilder.build();
		Stream<Element> actualStream = PefUtils.findMatchingDescendants(inputParent, elementTypes);
		List<Element> actualAsList = actualStream.collect(ImmutableList.toImmutableList());
		assertEquals(actualAsList.size(), expected.size(), "Not got the expected number of elements in the stream.");
		for (int i = 0; i < actualAsList.size(); i++) {
			XmlAssert.assertThat(actualAsList.get(i)).and(expected.get(i)).areIdentical();
		}
	}
	
}
