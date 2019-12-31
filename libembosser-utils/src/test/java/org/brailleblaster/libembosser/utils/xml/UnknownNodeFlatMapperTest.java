package org.brailleblaster.libembosser.utils.xml;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.base.Predicates;

public class UnknownNodeFlatMapperTest {
	@DataProvider(name="inputXmlProvider")
	public Object[][] inputXmlProvider() {
		return new Object[][] {
			{ "<p><b/></p>" },
			{ "<dx><i><emp/><op/></i><p/><b>ssd</b></dx>" },
		};
	}
	@Test(dataProvider="inputXmlProvider")
	public void testRecognisedNodeInToUnknownNodeFlatMapper(String inputXml) {
		Node node = null;
		try (InputStream is = new ByteArrayInputStream(inputXml.getBytes(Charsets.US_ASCII))) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			node = doc.getDocumentElement();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			fail("Problem parsing XML", e);
		}
		UnknownNodeFlatMapper fm = new UnknownNodeFlatMapper(Predicates.alwaysTrue());
		Stream<Node> actualStream = fm.apply(node);
		List<Node> actualAsList = actualStream.collect(Collectors.toList());
		assertEquals(actualAsList.size(), 1, "Stream should only have one element");
		assertSame(actualAsList.get(0), node);
	}
	@Test(dataProvider="inputXmlProvider")
	public void testUnrecognisedNodesInToUnknownNodeFlatMapper(String inputXml) {
		Node node = null;
		try (InputStream is = new ByteArrayInputStream(inputXml.getBytes(Charsets.US_ASCII))) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			node = doc.getDocumentElement();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			fail("Problem parsing XML", e);
		}
		UnknownNodeFlatMapper fm = new UnknownNodeFlatMapper(Predicates.alwaysFalse());
		Stream<Node> actualStream = fm.apply(node);
		assertEquals(actualStream.count(), 0l, "Stream expected to be empty");
	}
	@Test(dataProvider="inputXmlProvider")
	public void testUnrecognisedParentInToUnknownNodeFlatMapper(String inputXml) {
		Node node = null;
		try (InputStream is = new ByteArrayInputStream(inputXml.getBytes(Charsets.US_ASCII))) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			node = doc.getDocumentElement();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			fail("Problem parsing XML", e);
		}
		// Make final reference to node for lambda.
		final Node fNode = node;
		UnknownNodeFlatMapper fm = new UnknownNodeFlatMapper(n -> !n.equals(fNode));
		Stream<Node> actualStream = fm.apply(node);
		List<Node> actualAsList = actualStream.collect(Collectors.toList());
		NodeList expectedNodes = node.getChildNodes();
		assertEquals(actualAsList.size(), expectedNodes.getLength(), "Stream length does not match expected length");
		for (int i = 0; i < expectedNodes.getLength(); i++) {
			assertEquals(actualAsList.get(i), expectedNodes.item(i), String.format("Item at index %d does not match", i));
		}
	}
	@Test(dataProvider="inputXmlProvider")
	public void testUnrecognisedParentsRecurseInToUnknownNodeFlatMapper(String inputXml) {
		Node node = null;
		try (InputStream is = new ByteArrayInputStream(inputXml.getBytes(Charsets.US_ASCII))) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			node = doc.getDocumentElement();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			fail("Problem parsing XML", e);
		}
		final List<Node> recognisedNodes = new ArrayList<>();
		NodeList childrenLevel1 = node.getChildNodes();
		for (int i = 0; i < childrenLevel1.getLength(); i++) {
			Node child = childrenLevel1.item(i);
			if (child.hasChildNodes()) {
				NodeList childrenLevel2 = child.getChildNodes();
				for (int j = 0; j < childrenLevel2.getLength(); j++) {
					recognisedNodes.add(childrenLevel2.item(j));
				}
			} else {
				recognisedNodes.add(child);
			}
		}
		UnknownNodeFlatMapper fm = new UnknownNodeFlatMapper(recognisedNodes::contains);
		Stream<Node> actualStream = fm.apply(node);
		List<Node> actualAsList = actualStream.collect(Collectors.toList());
		assertEquals(actualAsList, recognisedNodes);
	}
}
