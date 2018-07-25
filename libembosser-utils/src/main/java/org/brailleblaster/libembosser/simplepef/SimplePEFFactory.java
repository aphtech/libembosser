package org.brailleblaster.libembosser.simplepef;

import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.PEFFactory;

public class SimplePEFFactory implements PEFFactory {

	@Override
	public PEFDocument createPEF(String identifier, String version) {
		return new PEFDocumentImpl(identifier);
	}

}