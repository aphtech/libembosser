package org.brailleblaster.libembosser.utils.pef.simple;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.PEFFactory;
import org.brailleblaster.libembosser.pef.PEFInputException;
import org.brailleblaster.libembosser.pef.PEFOutputException;
import org.brailleblaster.libembosser.utils.pef.simple.PEFDocumentImpl;
import org.brailleblaster.libembosser.utils.pef.simple.SimplePEFFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;

public class PEFDocumentTest {
	@DataProvider(name="pefProvider")
	private Iterator<Object[]> pefProvider() {
		List<Object[]> data = new ArrayList<>();
		PEFDocument pef = new PEFDocumentImpl("TestPEF0001");
		pef.getMeta().setTitle("Basic test PEF document");
		String expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">"
				+ "<head><meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
				+ "<dc:format>application/x-pef+xml</dc:format>"
				+ "<dc:identifier>TestPEF0001</dc:identifier>"
				+ "<dc:title>Basic test PEF document</dc:title>"
				+ "</meta></head>"
				+ "<body>"
				+ "<volume cols=\"1\" duplex=\"false\" rowgap=\"0\" rows=\"1\">"
				+ "<section>"
				+ "<page>"
				+ "</page>"
				+"</section>"
				+ "</volume>"
				+ "</body>"
				+ "</pef>";
		data.add(new Object[] {pef, expected});
		pef = new PEFDocumentImpl("TestPEF0002");
		pef.getMeta().setTitle("Basic default marshalling PEF");
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">"
				+ "<head><meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
				+ "<dc:format>application/x-pef+xml</dc:format>"
				+ "<dc:identifier>TestPEF0002</dc:identifier>"
				+ "<dc:title>Basic default marshalling PEF</dc:title>"
				+ "</meta></head>"
				+ "<body>"
				+ "<volume cols=\"1\" duplex=\"false\" rowgap=\"0\" rows=\"1\">"
				+ "<section>"
				+ "<page>"
				+ "</page>"
				+"</section>"
				+ "</volume>"
				+ "</body>"
				+ "</pef>";
		data.add(new Object[] {pef, expected});
		return data.iterator();
	}
	@Test(dataProvider="pefProvider")
	public void testMarshalPEF(PEFDocument pef, String expected) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			pef.save(os);
		} catch (PEFOutputException e) {
			fail("Saving the PEF gave an error", e);
		}
		String actual = new String(os.toByteArray(), Charsets.UTF_8);
		assertEquals(actual, expected);
	}
	@Test(dataProvider="pefProvider")
	public void testUnmarshalPEF(PEFDocument pef, String xml) {
		PEFFactory factory = new SimplePEFFactory();
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8));
		PEFDocument result = null;
		try {
			result = factory.loadPEF(in);
		} catch (PEFInputException e) {
			fail("Problem loading PEF", e);
		}
		assertEquals(result, pef);
	}
}
