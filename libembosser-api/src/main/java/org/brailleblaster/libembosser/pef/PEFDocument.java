package org.brailleblaster.libembosser.pef;

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface PEFDocument {
	public static final String PEF_NAMESPACE = "http://www.daisy.org/ns/2008/pef";
	public static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
	public String getVersion();
	public Meta getMeta();
	public Volume appendnewVolume();
	public Volume insertnewVolume(int index);
	public Volume getVolume(int index);
	public int getVolumeCount();
	public void removeVolume(int index);
	public void removeVolume(Volume vol);
	public default void save(OutputStream os) throws PEFOutputException {
		DefaultPEFWriter.write(this, os);
	}
}
