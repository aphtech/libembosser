package org.brailleblaster.libembosser.pef;

import java.io.InputStream;
import java.util.Arrays;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

class DefaultPEFReader {
	static class PEFFilter implements StreamFilter {
		private String[] pefNames = new String[] { "pef", "head", "meta", "volume", "section", "page", "row" };
		private String[] dcNames = new String[] { "format", "identifier", "title" };
		private String[] extpefnames = new String[] { "graphic" };
		@Override
		public boolean accept(XMLStreamReader reader) {
			switch(reader.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				return checkStartElement(XMLStreamReader reader);
			case XMLStreamConstants.END_ELEMENT:
				return checkEndElement(XMLStreamReader reader);
			default:
				return true;
			}
		}
		private boolean checkStartElement(XMLStreamReader reader) {
			switch(reader.getNamespaceURI()) {
			case PEFDocument.PEF_NAMESPACE:
				return Arrays.stream(pefNames).anyMatch(e -> e.equals(reader.getLocalName()));
			case PEFDocument.DC_NAMESPACE:
				return Arrays.stream(dcNames).anyMatch(e -> e.equals(reader.getLocalName()));
			default:
				return false;
			}
		}
		private boolean checkEndElement(XMLStreamReader reader) {
			return false;
		}
		private boolean checkElementName(XMLStreamReader reader) {
			return false;
		}
	}
	private XMLStreamReader reader;
	private PEFFactory factory;
	private DefaultPEFReader(PEFFactory factory, XMLStreamReader reader) {
		this.reader = reader;
		this.factory = factory;
	}
	private void close() {
		try {
			reader.close();
		} catch (XMLStreamException e) {
			// Nothing to do
		}
	}
	static PEFDocument read(PEFFactory factory, InputStream is) throws PEFInputException {
		DefaultPEFReader dr = null;
		PEFDocument doc = null;
		XMLInputFactory inFactory = XMLInputFactory.newInstance();
		try {
			XMLStreamReader reader = inFactory.createXMLStreamReader(is, "utf-8");
			dr = new DefaultPEFReader(factory, inFactory.createFilteredReader(reader, new PEFFilter()));
			doc = dr.readDocument();
		} catch (XMLStreamException e) {
			throw new PEFInputException("Problem creating PEF", e);
		} finally {
			if (dr != null) {
				dr.close();
			}
		}
		if (doc == null) throw new PEFInputException("Input is not a PEF");
		return doc;
	}
	private PEFDocument readDocument() throws XMLStreamException {
		PEFDocument doc = null;
		int event = reader.getEventType();
		while (true) {
			switch(event) {
			case XMLStreamConstants.START_ELEMENT:
				if (PEFDocument.PEF_NAMESPACE.equals(reader.getNamespaceURI()) && "pef".equals(reader.getLocalName())) {
					doc = readPEF();
				}
				break;
			default:
				// Do nothing
			}
			if (!reader.hasNext()) {
				break;
			}
			event = reader.next();
		}
		return doc;
	}
	private PEFDocument readPEF() {
		String version = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "version");
		// We create the PEF with a temp identifier and change it later when we find it in the document.
		PEFDocument doc = version == null ? factory.createPEF("TempID") : factory.createPEF("TempID", version);
		
		return doc;
	}
}
