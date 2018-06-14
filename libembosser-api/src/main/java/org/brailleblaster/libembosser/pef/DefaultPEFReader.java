package org.brailleblaster.libembosser.pef;

import java.io.InputStream;
import java.util.Arrays;

import javax.xml.namespace.QName;
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
				return checkStartElement(reader);
			case XMLStreamConstants.END_ELEMENT:
				return checkEndElement(reader);
			default:
				return false;
			}
		}
		private boolean checkStartElement(XMLStreamReader reader) {
			return checkElementName(reader);
		}
		private boolean checkEndElement(XMLStreamReader reader) {
			return checkElementName(reader);
		}
		private boolean checkElementName(XMLStreamReader reader) {
			switch(reader.getNamespaceURI()) {
			case PEFDocument.PEF_NAMESPACE:
				return Arrays.stream(pefNames).anyMatch(e -> e.equals(reader.getLocalName()));
			case PEFDocument.DC_NAMESPACE:
				return Arrays.stream(dcNames).anyMatch(e -> e.equals(reader.getLocalName()));
			default:
				return false;
			}
		}
		
	}
	private static abstract class BaseElementHandler {
		public abstract PEFDocument readElement(PEFDocument doc, XMLStreamReader reader) throws XMLStreamException, PEFInputException;
		protected PEFDocument readChildElements(PEFDocument doc, XMLStreamReader reader) throws XMLStreamException, PEFInputException {
			PEFDocument curDoc = doc;
			boolean continueLoop = true;
			while (reader.hasNext() && continueLoop) {
				int event = reader.nextTag();
				switch(event) {
				case XMLStreamConstants.START_ELEMENT:
					curDoc = onChildElement(curDoc, reader);
					break;
				case XMLStreamConstants.END_ELEMENT:
					// Should be end of our element due to onChildElement contract.
					continueLoop = false;
					break;
				}
			}
			return curDoc;
		}
		protected PEFDocument readChildNodes(PEFDocument doc, XMLStreamReader reader) throws XMLStreamException, PEFInputException {
			PEFDocument curDoc = doc;
			boolean continueLoop = true;
			while (reader.hasNext() && continueLoop) {
				int event = reader.next();
				switch(event) {
				case XMLStreamConstants.START_ELEMENT:
					curDoc = onChildElement(curDoc, reader);
					break;
				case XMLStreamConstants.END_ELEMENT:
					// Should be at end of current element because of onChildElement contract.
					continueLoop = false;
					break;
				case XMLStreamConstants.CHARACTERS:
					curDoc = onChildText(curDoc, reader);
					break;
				case XMLStreamConstants.CDATA:
					curDoc = onChildCData(curDoc, reader);
					break;
					default:
						// Nothing to do here
						break;
				}
			}
			return curDoc;
		}
		/** Read the child element and content from the reader.
		 * 
		 * This method should consume all the events from the reader which relate to the child element and any of its descendants. When this method returns it should leave the reader with the current event being the END_ELEMENT of the child element, so that reader.next() returns the first event after the child element.
		 * The default implementation skips the element.
		 * 
		 * @param doc The current PEF document being built.
		 * @param reader The reader object for the XML.
		 * @return The PEF document as after processing the child element.
		 * @throws XMLStreamException 
		 * @throws PEFInputException 
		 */
		protected PEFDocument onChildElement(PEFDocument doc, XMLStreamReader reader) throws XMLStreamException, PEFInputException {
			int depth = 1;
			while (reader.hasNext() && depth > 0) {
				int event = reader.nextTag();
				switch(event) {
				case XMLStreamConstants.START_ELEMENT:
					depth++;
					break;
				case XMLStreamConstants.END_ELEMENT:
					depth--;
					break;
					default:
						// Should not happen
						break;
				}
			}
			return doc;
		}
		/** Read the child text node.
		 * 
		 * This method is called when a child text node is encountered. This method should leave the reader object pointing at the event representing the text node, so that reader.next() will return the first event following the text node.
		 * The default implementation simply skips the text node.
		 * 
		 * @param doc The PEF document as before processing this child text node.
		 * @param reader The reader object representing the XML.
		 * @return The PEF document as after processing the text node.
		 */
		protected PEFDocument onChildText(PEFDocument doc, XMLStreamReader reader) throws PEFInputException, XMLStreamException {
			return doc;
		}
		/** Read the child CDATA node.
		 * 
		 * This method is called when a child CDATA node is encountered. This method should leave the reader object pointing at the event representing the CDATA node, so that reader.next() will return the first event following the CDATA node.
		 * The default implementation skips the CDATA.
		 * 
		 * @param doc The PEF document as before processing this child CDATA node.
		 * @param reader The reader object representing the XML.
		 * @return The PEF document as after processing the CDATA node.
		 */
		protected PEFDocument onChildCData(PEFDocument doc, XMLStreamReader reader) throws PEFInputException, XMLStreamException {
			return doc;
		}
	}
	private static class ChildElementDefinition {
		private final String ns;
		private final String name;
		private final int min;
		private final int max;
		private BaseElementHandler handler;
		public ChildElementDefinition(String ns, String name, int min, BaseElementHandler reader) {
			this(ns, name, min, Integer.MAX_VALUE, reader);
		}
		public ChildElementDefinition(String ns, String name, int min, int max, BaseElementHandler reader) {
			this.ns = ns;
			this.name = name;
			this.min = min;
			this.max = max;
			this.handler = reader;
		}
		public String getNS() {
			return ns;
		}
		public String getName() {
			return name;
		}
		public int getMin() {
			return min;
		}
		public int getMax() {
			return max;
		}
		public BaseElementHandler getHandler() {
			return handler;
		}
	}
	private static class ContainerElementHandler extends BaseElementHandler {
		private ChildElementDefinition[] childDefs;
		private int[] occurrences; 
		public ContainerElementHandler(ChildElementDefinition... childDefs) {
			this.childDefs = childDefs;
			this.occurrences = new int[childDefs.length];
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Ensure occurrences are all 0, protect against problems of reuse.
			Arrays.fill(occurrences, 0);
			return readChildElements(doc, reader);
		}
		@Override
		protected PEFDocument onChildElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			QName elemName = reader.getName();
			for (int i = 0; i < childDefs.length; i++) {
				ChildElementDefinition curDef = childDefs[i];
				if (new QName(curDef.getNS(), curDef.getName()).equals(elemName)) {
					occurrences[i]++;
					return curDef.getHandler().readElement(doc, reader);
				}
			}
			// Have not found any matching child definition so element should not be permitted
			throw new PEFInputException("Unexpected element");
		}
	}
	private static class PEFElementHandler extends BaseElementHandler {
		private PEFFactory factory;
		private BaseElementHandler headHandler = null;
		private BaseElementHandler bodyHandler = null;
		public PEFElementHandler(PEFFactory factory) {
			this.factory = factory;
			final ChildElementDefinition dcIdentifierDef = new ChildElementDefinition(PEFDocument.DC_NAMESPACE, "identifier", 1, 1, null);
			final ChildElementDefinition dcTitleDef = new ChildElementDefinition(PEFDocument.DC_NAMESPACE, "title", 0, 1, null);
			final ChildElementDefinition metaDef = new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "meta", 1, 1, new ContainerElementHandler(dcIdentifierDef, dcTitleDef));
			headHandler = new ContainerElementHandler(metaDef);
			
			bodyHandler = new BodyElementHandler();
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader) throws XMLStreamException, PEFInputException {
			// Get the version
			String version = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "version");
			// We expect doc to be null, we ignore it.
			// We set a temp ID and set it more correctly later.
			PEFDocument curDoc = version == null ? PEFFactory.getInstance().createPEF("TempID") : PEFFactory.getInstance().createPEF("TempID", version);
			curDoc = readChildElements(curDoc, reader);
			return curDoc;
		}
		@Override
		public PEFDocument onChildElement(PEFDocument doc, XMLStreamReader reader) throws PEFInputException, XMLStreamException {
			switch(reader.getLocalName()) {
			case "head":
				return headHandler.readElement(doc, reader);
			case "body":
				return bodyHandler.readElement(doc, reader);
			default:
				throw new PEFInputException("Unexpected element");
			}
		}
	}
	private static class BodyElementHandler extends ContainerElementHandler {
		public BodyElementHandler() {
			super(new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "volume", 1, new VolumeElementHandler()));
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Check that the initial PEF has the minimal PEF volume
			int initialVols = doc.getVolumeCount();
			PEFDocument result = super.readElement(doc, reader);
			// Minimal PEF documents must have at least one volume, so remove the initial volumes (IE. VolumeElementHandler creates new volumes for each element rather than using the initial volumes)
			while (result.getVolumeCount() > 1 && initialVols > 0) {
				result.removeVolume(0);
				initialVols--;
			}
			return result;
		}
	}
	private static class VolumeElementHandler extends ContainerElementHandler {
		public VolumeElementHandler() {
			super(new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "section", 1, null));
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Add a new volume
			Volume vol = doc.getVolume(doc.appendnewVolume();
			// Make sure the volume has correct cols, rows, etc
			String value = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "cols");
			vol.setCols(Integer.valueOf(value));
			value = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "rows");
			vol.setRows(Integer.valueOf(value));
			value = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "rowgap");
			vol.setRowGap(Integer.valueOf(value));
			value = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "duplex");
			vol.setDuplex(Boolean.valueOf(value));
			return super.readElement(doc, reader);
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
	private PEFDocument readPEF() throws XMLStreamException {
		String version = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "version");
		// We create the PEF with a temp identifier and change it later when we find it in the document.
		PEFDocument doc = version == null ? factory.createPEF("TempID") : factory.createPEF("TempID", version);
		while (reader.hasNext()) {
			int event = reader.nextTag();
			switch(event) {
			case XMLStreamConstants.START_ELEMENT:
				if ("head".equals(reader.getLocalName())) {
					readHead(doc);
				}
			}
		}
		return doc;
	}
	private void readHead(PEFDocument doc) {
		
	}
}
