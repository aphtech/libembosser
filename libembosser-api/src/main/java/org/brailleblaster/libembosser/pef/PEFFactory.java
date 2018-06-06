package org.brailleblaster.libembosser.pef;

import java.io.InputStream;
import java.util.ServiceLoader;

public interface PEFFactory {
	public PEFDocument createPEF(String identifier, String version);
	public default PEFDocument createPEF(String identifier) {
		return createPEF(identifier, "2008-1");
	}
	public default PEFDocument loadPEF(InputStream in) {
		// We create with a temp ID, change it when we load the XML
		PEFDocument doc = createPEF("TempID");
		return doc;
	}
	public static PEFFactory getInstance() {
		return PEFFactoryHelper.getinstance().loadPEFFactory();
	}
}
