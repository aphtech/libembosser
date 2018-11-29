package org.brailleblaster.libembosser.drivers.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartVolumeEvent;
import org.brailleblaster.libembosser.utils.PEFElementType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;

public class DocumentParser {
	
	private final static DocumentBuilder docBuilder;
	static {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Problem creating XML document builder", e);
		}
	}
	/**
	 * Parse the BRF passing the document events to handler.
	 * 
	 * @param input The InputStream of the BRF.
	 * @param handler The handler to recieve document events.
	 * @throws IOException Thrown when there is a problem reading the input stream. Should this happen then the handler probably will not be in the READY state and so cannot be reused without being reset.
	 */
	public void parseBrf(InputStream input, DocumentHandler handler) throws IOException {
		InputStream bufferedInput = new BufferedInputStream(input);
		ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream(100);
		int newLines = 0;
		int newPages = 0;
		int prevByte = -1;
		handler.onEvent(new StartDocumentEvent());;
		handler.onEvent(new StartVolumeEvent());;
		handler.onEvent(new StartSectionEvent());;
		handler.onEvent(new StartPageEvent());;
		int readByte;
		while ((readByte = bufferedInput.read()) >= 0) {
			switch(readByte) {
			case '\f':
				newLines =0;
				++newPages;
				createLineEvents(handler, lineBuffer);
				break;
			case '\n':
				if (prevByte != '\r') {
					++newLines;
					createLineEvents(handler, lineBuffer);
				}
				break;
			case '\r':
				++newLines;
				createLineEvents(handler, lineBuffer);
				break;
			default:
				while (newPages > 0) {
					handler.onEvent(new EndPageEvent());
					handler.onEvent(new StartPageEvent());
					--newPages;
				}
				while (newLines > 1) {
					handler.onEvent(new StartLineEvent());
					handler.onEvent(new EndLineEvent());
					--newLines;
				}
				newLines = 0;
				lineBuffer.write(readByte);
			}
			prevByte = readByte;
		}
		createLineEvents(handler, lineBuffer);
		handler.onEvent(new EndPageEvent());;
		handler.onEvent(new EndSectionEvent());;
		handler.onEvent(new EndVolumeEvent());;
		handler.onEvent(new EndDocumentEvent());;
	}

	private void createLineEvents(DocumentHandler handler, ByteArrayOutputStream lineBuffer)
			throws UnsupportedEncodingException {
		if (lineBuffer.size() > 0) {
			handler.onEvent(new StartLineEvent());
			handler.onEvent(new BrailleEvent(lineBuffer.toString(Charsets.US_ASCII.name())));
			handler.onEvent(new EndLineEvent());
		}
		lineBuffer.reset();
	}

	public void parsePef(InputStream input, DocumentHandler handler) throws IOException, SAXException {
		parsePef(docBuilder.parse(input), handler);
	}

	public void parsePef(Document inputDoc, DocumentHandler handler) {
		Element root = inputDoc.getDocumentElement();
		if (root != null && Optional.ofNullable(PEFElementType.PEF).equals(PEFElementType.findElementType(root))) {
			processPefElement((Element)root, handler);
		}
	}

	private void processPefElement(Element pefNode, DocumentHandler handler) {
		Deque<Node> nodeStack = new LinkedList<>();
		Node nextNode = pefNode;
		boolean descend = true;
		do {
			// Add any next node to the stack.
			if (nextNode != null) {
				nodeStack.push(nextNode);
				descend = enterNode(nextNode, handler);
			} else {
				descend = false;
			}
			Node curNode = nodeStack.peek();
			if (descend) {
				// Try and descend
				nextNode = curNode.getFirstChild();
			} else {
				nextNode = null;
			}
			// When not able to descend continue to next sibling
			if (nextNode == null) {
				nextNode = curNode.getNextSibling();
				// Also remove the current node from the stack as we will be leaving the node
				nodeStack.pop();
				exitNode(curNode, handler);
			}
		} while (!nodeStack.isEmpty());
	}
	private boolean enterNode(Node node, DocumentHandler handler) {
		boolean result = true;
		if (node instanceof Element) {
			Optional<PEFElementType> elementType = PEFElementType.findElementType((Element)node);
			if (elementType.isPresent()) {
				switch(elementType.get()) {
				case BODY:
					handler.onEvent(new StartDocumentEvent());
					break;
				case VOLUME:
					handler.onEvent(new StartVolumeEvent());
					break;
				case SECTION:
					handler.onEvent(new StartSectionEvent());;
					break;
				case PAGE:
					handler.onEvent(new StartPageEvent());;
					break;
				case ROW:
					handler.onEvent(new StartLineEvent());
					NodeList children = node.getChildNodes();
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < children.getLength(); ++i) {
						Node child = children.item(i);
						if (child instanceof Text) {
							sb.append(child.getNodeValue());
						}
					}
					handler.onEvent(new BrailleEvent(sb.toString().trim()));
					result = false;
					break;
				case HEAD:
					result = false;
					break;
				default:
					break;
				}
			}
		}
		return result;
	}
	private void exitNode(Node node, DocumentHandler handler) {
		if (node instanceof Element) {
			PEFElementType.findElementType((Element)node).ifPresent(t -> {
				switch(t) {
				case BODY:
					handler.onEvent(new EndDocumentEvent());
					break;
				case VOLUME:
					handler.onEvent(new EndVolumeEvent());;
					break;
				case SECTION:
					handler.onEvent(new EndSectionEvent());;
					break;
				case PAGE:
					handler.onEvent(new EndPageEvent());;
					break;
				case ROW:
					handler.onEvent(new EndLineEvent());;
					break;
				default:
					break;
				}
			});
		}
	}
}
