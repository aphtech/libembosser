package org.brailleblaster.libembosser.utils.xml;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class DocumentUtils {
	public static void prettyPrintDOM(Document pef, OutputStream os) {
		DOMImplementation domImpl = pef.getImplementation();
		if (domImpl.hasFeature("LS", "3.0") &&domImpl.hasFeature("Core", "2.0")) {
			DOMImplementationLS implLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
			LSSerializer lsSerializer = implLS.createLSSerializer();
			if (lsSerializer.getDomConfig().canSetParameter("format-pretty-print", Boolean.TRUE)) {
				lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
			}
			LSOutput lsOut = implLS.createLSOutput();
			lsOut.setEncoding(StandardCharsets.UTF_8.name());
			lsOut.setByteStream(os);
			lsSerializer.write(pef, lsOut);
		} else {
			// We probably will never get here, but cover ourselves.
			throw new UnsupportedOperationException("No suitable XML implementation");
		}
	}
}
