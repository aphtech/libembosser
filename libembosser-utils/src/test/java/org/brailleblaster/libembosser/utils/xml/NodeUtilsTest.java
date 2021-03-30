package org.brailleblaster.libembosser.utils.xml;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlunit.assertj.XmlAssert;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class NodeUtilsTest {
	@DataProvider(name="findMatchingDescendantsProvider")
	public Object[][] findMatchingDescendantsProvider() {
		return new Object[][] {
			{"<p/>", new String[] {}, (Predicate<Node>) o -> true},
			{"<p><i/><b/></p>", new String[] {}, (Predicate<Node>)n -> n instanceof Element && ((Element)n).getLocalName().equals("body")},
			{"<p>some text <b>More text</b></p>", new String[] {"<b>More text</b>"}, (Predicate<Node>)n -> n instanceof Element && ((Element)n).getLocalName().equals("b")},
		};
	}
	@Test(dataProvider="findMatchingDescendantsProvider")
	public void testFindMatchingDescendants(String parentXml, String[] descendantsXml, Predicate<Node> filter) {
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
		Stream<Node> actualStream = NodeUtils.findMatchingDescendants(inputParent, filter);
		List<Node> actualAsList = actualStream.collect(ImmutableList.toImmutableList());
		assertEquals(actualAsList.size(), expected.size(), "Not got the expected number of elements in the stream.");
		for (int i = 0; i < actualAsList.size(); i++) {
			XmlAssert.assertThat(actualAsList.get(i)).and(expected.get(i)).areIdentical();
		}
	}
}
