/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.utils.xml;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class NodeListUtilsTest {
	@DataProvider(name="inputXmlProvider")
	public Object[][] inputXmlProvider() {
		return new Object[][] {
			{ "<doc><d1><c1/></d1><pef xmlns=\"http://www.daisy.org/ns/2008/pef\"></pef></doc>" },
			{ "<p><b>ffi</b><br/></p>" },
		};
	}
	@Test(dataProvider="inputXmlProvider")
	public void testAsStream(String inputXml) {
		NodeList nodes = null;
		try (InputStream is = new ByteArrayInputStream(inputXml.getBytes(Charsets.US_ASCII))) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			nodes = doc.getDocumentElement().getChildNodes();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			fail("Problem parsing test XML", e);
		}
		Stream<Node> actualStream = NodeListUtils.asStream(nodes);
		List<Node> actualAsList = actualStream.collect(Collectors.toList());
		assertEquals(actualAsList.size(), nodes.getLength(), "Stream does not have correct length");
		for (int i = 0; i < nodes.getLength(); i++) {
			assertEquals(actualAsList.get(i), nodes.item(i), String.format("Item at index %d does not match", i));
		}
	}
	@Test(dataProvider="inputXmlProvider")
	public void testAsList(String inputXml) {
		NodeList nodes = null;
		try (InputStream is = new ByteArrayInputStream(inputXml.getBytes(Charsets.US_ASCII))) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			nodes = doc.getDocumentElement().getChildNodes();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			fail("Problem parsing test XML", e);
		}
		List<Node> actualAsList = NodeListUtils.asList(nodes);
		assertEquals(actualAsList.size(), nodes.getLength(), "Stream does not have correct length");
		for (int i = 0; i < nodes.getLength(); i++) {
			assertEquals(actualAsList.get(i), nodes.item(i), String.format("Item at index %d does not match", i));
		}
	}
}
