package org.brailleblaster.libembosser.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.brailleblaster.libembosser.utils.xml.NodeListUtils;
import org.brailleblaster.libembosser.utils.xml.NodeUtils;
import org.brailleblaster.libembosser.utils.xml.UnknownNodeFlatMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Charsets;
import com.google.common.primitives.Ints;


public final class PefUtils {
	private static final int[] LINE_ENDINGS = new int[] { '\r', '\n', '\f', -1 };
	/**
	 * Create a PEF from a BRF.
	 * 
	 * @param brf The BRF as a Reader.
	 * @param id The identifier to be used for this document.
	 * @param cells The number of cells per line.
	 * @param lines The number of lines per page.
	 * @param duplex Whether this document should be considered duplex.
	 * @return A PEF Document containing the Braille from the BRF.
	 * @throws ParserConfigurationException When there is a problem initialising the DOM.
	 * @throws IOException When there is a problem reading the BRF.
	 */
	public static Document fromBrf(InputStream brf, String id, int cells, int lines, boolean duplex) throws ParserConfigurationException, IOException {
		return fromBrf(new BufferedReader(new InputStreamReader(brf, Charsets.US_ASCII)), id, cells, lines, duplex);
	}
	/**
	 * Create a PEF from a BRF.
	 * 
	 * @param brf The BRF as a Reader.
	 * @param id The identifier to be used for this document.
	 * @param cells The number of cells per line.
	 * @param lines The number of lines per page.
	 * @param duplex Whether this document should be considered duplex.
	 * @return A PEF Document containing the Braille from the BRF.
	 * @throws ParserConfigurationException When there is a problem initialising the DOM.
	 * @throws IOException When there is a problem reading the BRF.
	 */
	public static Document fromBrf(Reader brf, String id, int cells, int lines, boolean duplex) throws ParserConfigurationException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElementNS(PEFElementType.PEF.getNamespaceUri(), PEFElementType.PEF.getElementName());
		root.setAttribute("version", "2008-1");
		doc.appendChild(root);
		Element head = doc.createElementNS(PEFElementType.HEAD.getNamespaceUri(), PEFElementType.HEAD.getElementName());
		Element meta = doc.createElementNS(PEFElementType.META.getNamespaceUri(), PEFElementType.META.getElementName());
		Element dcFormat = doc.createElementNS(PEFElementType.DC_FORMAT.getNamespaceUri(), "dc:" + PEFElementType.DC_FORMAT.getElementName());
		dcFormat.setTextContent("application/x-pef+xml");
		meta.appendChild(dcFormat);
		Element dcIdentifier = doc.createElementNS(PEFElementType.DC_IDENTIFIER.getNamespaceUri(), "dc:" + PEFElementType.DC_IDENTIFIER.getElementName());
		dcIdentifier.setTextContent(id);
		meta.appendChild(dcIdentifier);
		head.appendChild(meta);
		root.appendChild(head);
		Element body = doc.createElementNS(PEFElementType.BODY.getNamespaceUri(), PEFElementType.BODY.getElementName());
		Element volume = doc.createElementNS(PEFElementType.VOLUME.getNamespaceUri(), PEFElementType.VOLUME.getElementName());
		volume.setAttribute("cols", Integer.toString(cells));
		volume.setAttribute("rows", Integer.toString(lines));
		volume.setAttribute("duplex", Boolean.toString(duplex));
		Element section = doc.createElementNS(PEFElementType.SECTION.getNamespaceUri(), PEFElementType.SECTION.getElementName());
		int prevChar = 0;
		int curChar = 0;
		do {
			Element page = doc.createElementNS(PEFElementType.PAGE.getNamespaceUri(),
					PEFElementType.PAGE.getElementName());
			Deque<String> rows = new LinkedList<>();
			do {
				StringBuffer buf = new StringBuffer();
				prevChar = curChar;
				while (!Ints.contains(LINE_ENDINGS, (curChar = brf.read()))) {
					buf.append((char) curChar);
				}
				// Handle the \r\n (dos) line ending.
				if (!(prevChar == (int)'\r' && curChar == (int)'\n')) {
					rows.addLast(BrailleMapper.ASCII_TO_UNICODE_FAST.map(buf.toString()));
				} 
			} while (curChar != (int)'\f' && curChar != -1);
			// Trim the trailing empty lines
			while (!rows.isEmpty() && rows.getLast().isEmpty()) {
				rows.removeLast();
			}
			for (String row: rows) {
				Element rowElem = doc.createElementNS(PEFElementType.ROW.getNamespaceUri(), PEFElementType.ROW.getElementName());
				rowElem.setTextContent(row);
				page.appendChild(rowElem);
			}
			section.appendChild(page);
		} while (curChar != -1);
		volume.appendChild(section);
		body.appendChild(volume);
		root.appendChild(body);
		return doc;
	}
	
	public static Stream<Element> findMatchingDescendants(Element element, PEFElementType... elementTypes) {
		List<PEFElementType> typesList = Arrays.asList(elementTypes);
		return NodeUtils.findMatchingDescendants(element, n -> n instanceof Element && PEFElementType.findElementType((Element)n).filter(typesList::contains).isPresent()).map(n -> (Element)n);
	}
}
