package org.brailleblaster.libembosser.pef;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;

class DefaultPEFReader {
	static class PEFFilter implements StreamFilter {
		private String[] pefNames = new String[] { "pef", "head", "meta", "body", "volume", "section", "page", "row" };
		private String[] dcNames = new String[] { "coverage", "contributor", "creator", "date", "description", "format", "identifier", "language", "publisher", "relation", "rights", "source", "subject", "title", "type" };
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
				int event = reader.next();
				switch(event) {
				case XMLStreamConstants.START_ELEMENT:
					depth++;
					break;
				case XMLStreamConstants.END_ELEMENT:
					depth--;
					break;
					default:
						// Can ignore other events
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
			this.name = checkNotNull(name);
			this.min = min;
			this.max = max;
			this.handler = checkNotNull(reader);
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
			return readChildElements(doc, reader);
		}
		@Override
		protected PEFDocument readChildElements(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Ensure occurrences are all 0, protect against problems of reuse.
			Arrays.fill(occurrences, 0);
			return super.readChildElements(doc, reader);
		}
		@Override
		protected PEFDocument onChildElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			QName elemName = reader.getName();
			for (int i = 0; i < childDefs.length; i++) {
				ChildElementDefinition curDef = childDefs[i];
				if (Objects.equal(curDef.getName(), elemName.getLocalPart())) {
					occurrences[i]++;
					return curDef.getHandler().readElement(doc, reader);
				}
			}
			// Have not found any matching child definition so element should not be permitted
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < childDefs.length; i++) {
				sb.append(String.format("%s:%s", childDefs[i].getNS(), childDefs[i].getName()));
				sb.append(", ");
			}
			sb.append("] ");
			sb.append(getClass().getName());
			throw new PEFInputException(String.format("Unexpected element: %s:%s %s", elemName.getNamespaceURI(), elemName.getLocalPart(), sb.toString()));
		}
	}
	private static class PEFElementHandler extends ContainerElementHandler {
		private PEFFactory factory;
		public PEFElementHandler(PEFFactory factory) {
			super(new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "head", 1, 1, new ContainerElementHandler(new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "meta", 1, 1, new MetaElementHandler()))),
					new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "body", 1, 1, new BodyElementHandler()));
			this.factory = checkNotNull(factory);
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader) throws XMLStreamException, PEFInputException {
			// Get the version
			String version = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "version");
			// We expect doc to be null, we ignore it.
			// We set a temp ID and set it more correctly later.
			PEFDocument curDoc = version == null ? factory.createPEF("TempID") : PEFFactory.getInstance().createPEF("TempID", version);
			curDoc = super.readElement(curDoc, reader);
			return curDoc;
		}
	}
	private static class RowElementHandler extends BaseElementHandler {
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Create a row
			Volume vol = doc.getVolume(doc.getVolumeCount() - 1);
			Section section = vol.getSection(vol.getSectionCount() - 1);
			Page page = section.getPage(section.getPageCount() - 1);
			Row row = page.appendNewRow();
			String value = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "rowgap");
			row.setRowGap(Integer.valueOf(value));
			String braille = reader.getElementText();
			row.setBraille(braille);
			return doc;
		}
	}
	private static class MetaElementHandler extends BaseElementHandler {
		private static class ChildHandler {
			private int occurrence = 0;
			private final Range<Integer> required;
			private BiConsumer<Meta, String> f;
			public ChildHandler(Range<Integer> required, BiConsumer<Meta, String> f) {
				this.required = required;
				this.f = f;
			}
			public boolean checkOccurrences() {
				return required.contains(occurrence);
			}
			public void reset() {
				occurrence = 0;
			}
			public void onValue(Meta meta, String value) {
				f.accept(meta, value);
				occurrence++;
			}
		}
		private Map<String, ChildHandler> requiredElements;
		MetaElementHandler() {
			final ChildHandler zeroOrMoreHandler = new ChildHandler(Range.atLeast(0), (m, v) -> {
				// For now nothing
			});
			final Range<Integer> optionalRange = Range.closed(0, 1);
			final Range<Integer> mandatoryRange = Range.closed(1, 1);
			this.requiredElements = ImmutableMap.<String, ChildHandler>builder()
					.put("format", new ChildHandler(mandatoryRange, (m, v) -> {
						if (!"application/x-pef+xml".equals(v)) {
							throw new RuntimeException("Invalid value for format");
						}
						// We need not set the format as this should already be done.
					}))
					.put("identifier", new ChildHandler(mandatoryRange, (m, v) -> m.setIdentifier(v)))
					.put("title", new ChildHandler(optionalRange, (m, v) -> m.setTitle(v)))
					.put("creator", zeroOrMoreHandler)
					.put("subject", zeroOrMoreHandler)
					.put("description", new ChildHandler(optionalRange, (m, v) -> m.setDescription(v)))
					.put("publisher", zeroOrMoreHandler)
					.put("contributor", zeroOrMoreHandler)
					.put("date", new ChildHandler(optionalRange, (m, v) -> m.setDate(v)))
					.put("type", zeroOrMoreHandler)
					.put("source", zeroOrMoreHandler)
					.put("language", zeroOrMoreHandler)
					.put("relation", zeroOrMoreHandler)
					.put("coverage", zeroOrMoreHandler)
					.put("rights", zeroOrMoreHandler)
					.build();
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Reset all the handlers, eg. the counters.
			for (ChildHandler c: requiredElements.values()) {
				c.reset();
			}
			PEFDocument curDoc = this.readChildElements(doc, reader);
			for (Entry<String, ChildHandler> entry: requiredElements.entrySet()) {
				if (!entry.getValue().checkOccurrences()) {
					throw new PEFInputException(String.format("Incorrect number of occurrences for %s", entry.getKey()));
				}
			}
			return curDoc;
		}
		@Override
		protected PEFDocument onChildElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			ChildHandler c = requiredElements.getOrDefault(reader.getLocalName(), null);
			if (c != null) {
				Meta meta = doc.getMeta();
				String value = reader.getElementText();
				c.onValue(meta, value);
			} else {
				throw new PEFInputException("Unknown element");
			}
			return doc;
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
			super(new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "section", 1, new SectionHandler()));
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Add a new volume
			Volume vol = doc.appendnewVolume();
			// Make sure the volume has correct cols, rows, etc
			String value = checkNotNull(reader.getAttributeValue(null, "cols"));
			vol.setCols(Integer.valueOf(value));
			value = checkNotNull(reader.getAttributeValue(null, "rows"));
			vol.setRows(Integer.valueOf(value));
			value = checkNotNull(reader.getAttributeValue(null, "rowgap"));
			vol.setRowGap(Integer.valueOf(value));
			value = checkNotNull(reader.getAttributeValue(null, "duplex"));
			vol.setDuplex(Boolean.valueOf(value));
			// A volume must contain at least one section, therefore volumes are created with initial sections
			int initialSections = vol.getSectionCount();
			PEFDocument result = super.readElement(doc, reader);
			// Remove the initial sections from the volume
			while (vol.getSectionCount() > 1 && initialSections > 0) {
				vol.removeSection(0);
				initialSections--;
			}
			return result;
		}
	}
	private static class SectionHandler extends ContainerElementHandler {
		public SectionHandler() {
			super(new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "page", 1, new PageHandler()));
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// The current volume is the last volume
			Volume vol = doc.getVolume(doc.getVolumeCount() - 1);
			Section section = vol.appendNewSection();
			String value = reader.getAttributeValue(null, "cols");
			section.setCols(value == null ? null : Integer.valueOf(value));
			value = reader.getAttributeValue(null, "duplex");
			section.setDuplex(value == null ? null : Boolean.valueOf(value));
			value = reader.getAttributeValue(null, "rowgap");
			section.setRowGap(value == null ? null : Integer.valueOf(value));
			value = reader.getAttributeValue(null, "rows");
			section.setRows(value == null ? null : Integer.valueOf(value));
			int initialPages = section.getPageCount();
			PEFDocument result = super.readElement(doc, reader);
			// Remove the initial pages
			while (section.getPageCount() > 1 && initialPages > 0) {
				section.removePage(0);
				initialPages--;
			}
			return result;
		}
	}
	private static class PageHandler extends ContainerElementHandler {
		public PageHandler() {
			super(new ChildElementDefinition(PEFDocument.PEF_NAMESPACE, "row", 0, new RowElementHandler()));
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Last volume last section is where we are
			Volume vol = doc.getVolume(doc.getVolumeCount() - 1);
			Section section = vol.getSection(vol.getSectionCount() - 1);
			Page page = section.appendNewPage();
			String value = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "rowgap");
			page.setRowGap(value == null ? null : Integer.valueOf(value));
			int initialRows = page.getRowCount();
			PEFDocument result = super.readElement(doc, reader);
			while (page.getRowCount() > 0 && initialRows > 0) {
				page.removeRow(0);
				initialRows--;
			}
			return result;
		}
	}
	private XMLStreamReader reader;
	private PEFElementHandler pefHandler;
	private DefaultPEFReader(PEFFactory factory, XMLStreamReader reader) {
		this.pefHandler = new PEFElementHandler(factory);
		this.reader = reader;
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
			dr = new DefaultPEFReader(factory, reader);
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
	private PEFDocument readDocument() throws XMLStreamException, PEFInputException {
		PEFDocument doc = null;
		int event = reader.getEventType();
		while (true) {
			switch(event) {
			case XMLStreamConstants.START_ELEMENT:
				if (PEFDocument.PEF_NAMESPACE.equals(reader.getNamespaceURI()) && "pef".equals(reader.getLocalName())) {
					doc = pefHandler.readElement(doc, reader);
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
}
