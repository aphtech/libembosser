package org.brailleblaster.libembosser.pef;

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

class DefaultPEFWriter {
	private XMLStreamWriter writer;
	private PEFDocument doc;
	private DefaultPEFWriter(PEFDocument doc, XMLStreamWriter writer) {
		this.writer = writer;
		this.doc = doc;
	}
	private void close() {
		try {
			writer.close();
		} catch (XMLStreamException e) {
			// Nothing to do
		}
	}
	static void write(PEFDocument doc, OutputStream os) throws PEFOutputException {
		XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
		DefaultPEFWriter dw = null;
		try {
			dw = new DefaultPEFWriter(doc, outFactory.createXMLStreamWriter(os, "utf-8"));
			dw.writeDocument();
		} catch (XMLStreamException e) {
			throw new PEFOutputException("Problem writing PEF", e);
		} finally {
			if (dw != null) {
				dw.close();
			}
		}
	}
	private void writeDocument() throws XMLStreamException {
		writer.writeStartDocument("utf-8", "1.0");
		writer.writeStartElement("pef");
		writer.writeDefaultNamespace(PEFDocument.PEF_NAMESPACE);
		writer.writeAttribute("version", doc.getVersion());
		// Header
		writeHead();
		// Body
		writeBody();
		// End pef
		writer.writeEndElement();
		writer.writeEndDocument();
	}
	private void writeDCElement(String elementName, String value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writer.writeStartElement("dc", elementName, PEFDocument.DC_NAMESPACE);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}
	private void writeHead() throws XMLStreamException {
		final Meta meta = doc.getMeta();
		// Handle the head element
		writer.writeStartElement("head");
		// Then the nested meta element
		writer.writeStartElement("meta");
		writer.writeNamespace("dc", PEFDocument.DC_NAMESPACE);
		// Handle all contributors
		for (String v: meta.getContributors()) {
			writeDCElement("contributor", v);
		}
		// Handle all coverages
		for (String v: meta.getCoverages()) {
			writeDCElement("coverage", v);
		}
		// Handle all creators
		for (String v: meta.getCreators()) {
			writeDCElement("creator", v);
		}
		// Handle date
		writeDCElement("date", meta.getDate());
		// Handle description
		writeDCElement("description", meta.getDescription());
		// Handle format
		writeDCElement("format", meta.getFormat());
		// Handle the identifier
		writeDCElement("identifier", meta.getIdentifier());
		// Handle all languages
		for (String v: meta.getLanguages()) {
			writeDCElement("language", v);
		}
		// Handle all publishers
		for (String v: meta.getPublishers()) {
			writeDCElement("publisher", v);
		}
		// Handle all relations
		for (String v: meta.getRelations()) {
			writeDCElement("relation", v);
		}
		// Handle all rights
		for (String v: meta.getRights()) {
			writeDCElement("rights", v);
		}
		// Handle all sources
		for (String v: meta.getSources()) {
			writeDCElement("source", v);
		}
		// Handle all subjects
		for (String v: meta.getSubjects()) {
			writeDCElement("subject", v);
		}
		// Title
		writeDCElement("title", meta.getTitle());
		// Handle all types
		for (String v: meta.getTypes()) {
			writeDCElement("type", v);
		}
		// End meta
		writer.writeEndElement();
		//end head
		writer.writeEndElement();
	}
	private void writeBody() throws XMLStreamException {
		writer.writeStartElement("body");
		for (int i = 0; i < doc.getVolumeCount(); i++) {
			Volume vol = doc.getVolume(i);
			writeVolume(vol);
		}
		writer.writeEndElement();
	}
	private void writeVolume(Volume vol) throws XMLStreamException {
		writer.writeStartElement("volume");
		// As attributes are required for volume get the actual values.
		writer.writeAttribute("cols", Integer.toString(vol.getColsValue()));
		writer.writeAttribute("duplex", Boolean.toString(vol.getDuplexValue()));
		writer.writeAttribute("rowgap", Integer.toString(vol.getRowGapValue()));
		writer.writeAttribute("rows", Integer.toString(vol.getRowsValue()));
		
		for (int i = 0; i < vol.getSectionCount(); i++) {
			Section section = vol.getSection(i);
			writeSection(section);
		}
		writer.writeEndElement();
	}
	private void writeSection(Section section) throws XMLStreamException {
		writer.writeStartElement("section");
		String[] attrNames = new String[] { "cols", "duplex", "rowgap", "rows" };
		Object[] attrVals = new Object[] { section.getCols(), section.getDuplex(), section.getRowGap(), section.getRows() };
		for (int i = 0; i < attrNames.length; i++) {
			Object value = attrVals[i];
			if (value != null) {
				String name = attrNames[i];
				writer.writeAttribute(name, value.toString());
			}
		}
		
		for (int i = 0; i < section.getPageCount(); i++) {
			Page page = section.getPage(i);
			writePage(page);
		}
		writer.writeEndElement();
	}
	private void writePage(Page page) throws XMLStreamException {
		writer.writeStartElement("page");
		Integer rowGap = page.getRowGap();
		if (rowGap != null) {
			writer.writeAttribute("rowgap", rowGap.toString());
		}
		
		for (int i = 0; i < page.getRowCount(); i++) {
			Row row = page.getRow(i);
			writeRow(row);
		}
		writer.writeEndElement();
	}
	private void writeRow(Row row) throws XMLStreamException {
		writer.writeStartElement("row");
		Integer rowGap = row.getRowGap();
		if (rowGap != null) {
			writer.writeAttribute("rowgap", rowGap.toString());
		}
		
		String braille = row.getBraille();
		if (braille != null) {
			writer.writeCharacters(braille);
		}
		writer.writeEndElement();
	}
}