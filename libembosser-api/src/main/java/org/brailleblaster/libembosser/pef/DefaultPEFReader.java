package org.brailleblaster.libembosser.pef;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

class DefaultPEFReader {
	private static abstract class BaseElementHandler implements ThrowingBiFunction<PEFDocument, XMLStreamReader, PEFDocument, PEFInputException> {
		@Override
		public PEFDocument apply(PEFDocument arg1, XMLStreamReader arg2) throws PEFInputException {
			try {
				return readElement(arg1, arg2);
			} catch (XMLStreamException e) {
				throw new PEFInputException("Problem with XML", e);
			}
		}
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
	private static class ContainerElementHandler extends BaseElementHandler {
		private final Map<QName, ChildHandler<PEFDocument, XMLStreamReader, PEFDocument>> childDefMap;
		public ContainerElementHandler(Map<QName, ChildHandler<PEFDocument, XMLStreamReader, PEFDocument>> childDefs) {
			this.childDefMap = childDefs;
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			return readChildElements(doc, reader);
		}
		@Override
		protected PEFDocument readChildElements(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Ensure counters in child handlers are reset
			childDefMap.values().forEach(c -> c.reset());
			return super.readChildElements(doc, reader);
		}
		@Override
		protected PEFDocument onChildElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			QName elemName = reader.getName();
			if (childDefMap.containsKey(elemName)) {
				return childDefMap.get(elemName).onValue(doc, reader);
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				childDefMap.keySet().forEach(c -> sb.append(String.format("%s:%s, ", c.getNamespaceURI(), c.getLocalPart())));
				sb.append("] ");
				sb.append(getClass().getName());
				throw new PEFInputException(String.format("Unexpected element: %s:%s %s", elemName.getNamespaceURI(), elemName.getLocalPart(), sb.toString()));
			}
		}
	}
	private static class PEFElementHandler extends ContainerElementHandler {
		private PEFFactory factory;
		public PEFElementHandler(PEFFactory factory) {
			super(ImmutableMap.of(new QName(PEFDocument.PEF_NAMESPACE, "head"), new ChildHandler<>(Range.singleton(1), new ContainerElementHandler(ImmutableMap.of(new QName(PEFDocument.PEF_NAMESPACE, "meta"), new ChildHandler<>(Range.singleton(1), new MetaElementHandler())))),
					new QName(PEFDocument.PEF_NAMESPACE, "body"), new ChildHandler<>(Range.singleton(1), new BodyElementHandler())));
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
				throws PEFInputException, XMLStreamException {
			// Create a row
			Volume vol = doc.getVolume(doc.getVolumeCount() - 1);
			Section section = vol.getSection(vol.getSectionCount() - 1);
			Page page = section.getPage(section.getPageCount() - 1);
			Row row = page.appendNewRow();
			String value = reader.getAttributeValue(PEFDocument.PEF_NAMESPACE, "rowgap");
			row.setRowGap(value == null ? null : Integer.valueOf(value));
			String braille = reader.getElementText();
			row.setBraille(braille);
			return doc;
		}
	}
	private static class MetaElementHandler extends BaseElementHandler {
		private Map<String, DefaultPEFReader.ChildHandler<Meta, String, Void>> requiredElements;
		MetaElementHandler() {
			final Range<Integer> optionalRange = Range.closed(0, 1);
			final Range<Integer> mandatoryRange = Range.closed(1, 1);
			final Range<Integer> zeroOrMoreRange = Range.atLeast(0);
			this.requiredElements = ImmutableMap.<String, DefaultPEFReader.ChildHandler<Meta, String, Void>>builder()
					.put("format", new DefaultPEFReader.ChildHandler<Meta, String, Void>(mandatoryRange, (m, v) -> {
						if (!"application/x-pef+xml".equals(v)) {
							throw new RuntimeException("Invalid value for format");
						}
						// We need not set the format as this should already be done.
						return null;
					}))
					.put("identifier", new DefaultPEFReader.ChildHandler<Meta, String, Void>(mandatoryRange, (m, v) -> {
						m.setIdentifier(v);
						return null;
					}))
					.put("title", new DefaultPEFReader.ChildHandler<Meta, String, Void>(optionalRange, (m, v) -> {
						m.setTitle(v);
						return null;
					}))
					.put("creator", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m, v) -> {
						List<String> creators = Lists.newLinkedList(m.getCreators());
						creators.add(v);
						m.setCreators(creators);
						return null;
					}))
					.put("subject", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m, v) -> {
						List<String> subjects = Lists.newLinkedList(m.getSubjects());
						subjects.add(v);
						m.setSubjects(subjects);
						return null;
					}))
					.put("description", new DefaultPEFReader.ChildHandler<Meta, String, Void>(optionalRange, (m, v) -> {
						m.setDescription(v);
						return null;
					}))
					.put("publisher", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m,v) -> {
						List<String> publishers = Lists.newLinkedList(m.getPublishers());
						publishers.add(v);
						m.setPublishers(publishers);
						return null;
					}))
					.put("contributor", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m,v) -> {
						List<String> contributors = Lists.newLinkedList(m.getContributors());
						contributors.add(v);
						m.setContributors(contributors);
						return null;
					}))
					.put("date", new DefaultPEFReader.ChildHandler<Meta, String, Void>(optionalRange, (m, v) -> {
						m.setDate(v);
						return null;
					}))
					.put("type", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m, v) -> {
						List<String> types = Lists.newLinkedList(m.getTypes());
						types.add(v);
						m.setTypes(types);
						return null;
					}))
					.put("source", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m, v) -> {
						List<String> sources = Lists.newLinkedList(m.getSources());
						sources.add(v);
						m.setSources(sources);
						return null;
					}))
					.put("language", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m,v) -> {
						List<String> languages = Lists.newLinkedList(m.getLanguages());
						languages.add(v);
						m.setLanguages(languages);
						return null;
					}))
					.put("relation", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m, v) -> {
						List<String> relations = Lists.newLinkedList(m.getRelations());
						relations.add(v);
						m.setRelations(relations);
						return null;
					}))
					.put("coverage", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m, v) -> {
						List<String> coverages = Lists.newLinkedList(m.getCoverages());
						coverages.add(v);
						m.setCoverages(coverages);
						return null;
					}))
					.put("rights", new DefaultPEFReader.ChildHandler<Meta, String, Void>(zeroOrMoreRange, (m,v) -> {
						List<String> rights = Lists.newLinkedList(m.getRights());
						rights.add(v);
						m.setRights(rights);
						return null;
					}))
					.build();
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			// Reset all the handlers, eg. the counters.
			for (DefaultPEFReader.ChildHandler<Meta, String, Void> c: requiredElements.values()) {
				c.reset();
			}
			PEFDocument curDoc = this.readChildElements(doc, reader);
			for (Entry<String, DefaultPEFReader.ChildHandler<Meta,String, Void>> entry: requiredElements.entrySet()) {
				if (!entry.getValue().checkOccurrences()) {
					throw new PEFInputException(String.format("Incorrect number of occurrences for %s", entry.getKey()));
				}
			}
			return curDoc;
		}
		@Override
		protected PEFDocument onChildElement(PEFDocument doc, XMLStreamReader reader)
				throws XMLStreamException, PEFInputException {
			DefaultPEFReader.ChildHandler<Meta, String, Void> c = requiredElements.getOrDefault(reader.getLocalName(), null);
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
			super(ImmutableMap.of(new QName(PEFDocument.PEF_NAMESPACE, "volume"), new ChildHandler<>(Range.atLeast(1), new VolumeElementHandler())));
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
			super(ImmutableMap.of(new QName(PEFDocument.PEF_NAMESPACE, "section"), new ChildHandler<>(Range.atLeast(1), new SectionHandler())));
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
			super(ImmutableMap.of(new QName(PEFDocument.PEF_NAMESPACE, "page"), new ChildHandler<>(Range.atLeast(1), new PageHandler())));
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
			super(ImmutableMap.of(new QName(PEFDocument.PEF_NAMESPACE, "row"), new ChildHandler<>(Range.atLeast(0), new RowElementHandler())));
		}
		@Override
		public PEFDocument readElement(PEFDocument doc, XMLStreamReader reader)
				throws PEFInputException, XMLStreamException {
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
	private static class ChildHandler<T1, T2, R> {
		private int occurrence = 0;
		private final Range<Integer> required;
		private final ThrowingBiFunction<T1, T2, R, PEFInputException> f;
		public ChildHandler(Range<Integer> required, ThrowingBiFunction<T1, T2, R, PEFInputException> f) {
			this.required = required;
			this.f = f;
		}
		public boolean checkOccurrences() {
			return required.contains(occurrence);
		}
		public void reset() {
			occurrence = 0;
		}
		public R onValue(T1 meta, T2 value) throws PEFInputException {
			R result = f.apply(meta, value);
			occurrence++;
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
