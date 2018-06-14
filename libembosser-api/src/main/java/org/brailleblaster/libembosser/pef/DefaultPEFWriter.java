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
	private void writeHead() throws XMLStreamException {
		// Handle the head
		writer.writeStartElement("head");
		writer.writeStartElement("meta");
		writer.writeNamespace("dc", PEFDocument.DC_NAMESPACE);
		// Handle the identifier
		writer.writeStartElement("dc", "identifier", PEFDocument.DC_NAMESPACE);
		writer.writeCharacters(doc.getMeta().getIdentifier());
		// End Identifier
		writer.writeEndElement();
		// Title
		writer.writeStartElement("dc", "title", PEFDocument.DC_NAMESPACE);
		writer.writeCharacters(doc.getMeta().getTitle());
		// End title
		writer.writeEndElement();
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