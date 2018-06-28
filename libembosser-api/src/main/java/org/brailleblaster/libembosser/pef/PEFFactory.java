package org.brailleblaster.libembosser.pef;

import java.io.InputStream;

public interface PEFFactory {
	public PEFDocument createPEF(String identifier, String version);
	public default PEFDocument createPEF(String identifier) {
		return createPEF(identifier, "2008-1");
	}
	public default PEFDocument loadPEF(InputStream in) throws PEFInputException {
		PEFDocument doc = DefaultPEFReader.read(this, in);
		return doc;
	}
	public static PEFFactory getInstance() {
		return PEFFactoryHelper.getinstance().loadPEFFactory();
	}
}
