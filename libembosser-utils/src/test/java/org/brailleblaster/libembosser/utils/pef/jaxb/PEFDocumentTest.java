package org.brailleblaster.libembosser.utils.pef.jaxb;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;

import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.PEFOutputException;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;

public class PEFDocumentTest {
	@Test
	public void testPEFMarshalling() {
		PEFDocument pef = new PEFDocumentImpl("TestPEF0001");
		pef.getMeta().setTitle("Basic test PEF document");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		+ "<ns1:pef xmlns:ns1=\"http://www.daisy.org/ns/2008/pef\" xmlns:ns2=\"http://purl.org/dc/elements/1.1/\" ns1:version=\"2008-1\">"
				+ "<ns1:head>"
		+ "<ns1:meta>"
				+ "<ns2:format>application/x-pef+xml</ns2:format>"
		+ "<ns2:identifier>TestPEF0001</ns2:identifier>"
				+ "<ns2:title>Basic test PEF document</ns2:title>"
		+ "</ns1:meta>"
				+ "</ns1:head>"
		+ "<ns1:body>"
				+ "<ns1:volume ns1:rowgap=\"0\" ns1:cols=\"1\" ns1:rows=\"1\" ns1:duplex=\"false\">"
		+ "<ns1:section>"
				+ "<ns1:page/>"
				+ "</ns1:section>"
		+ "</ns1:volume>"
				+ "</ns1:body>"
		+ "</ns1:pef>";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			pef.save(os);
		} catch (PEFOutputException e) {
			fail("Saving the PEF gave an error", e);
		}
		String actual = new String(os.toByteArray(), Charsets.UTF_8);
		assertEquals(actual, expected);
	}
}
