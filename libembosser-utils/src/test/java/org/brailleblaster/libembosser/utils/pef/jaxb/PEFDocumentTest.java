package org.brailleblaster.libembosser.utils.pef.jaxb;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brailleblaster.libembosser.pef.Meta;
import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.PEFOutputException;
import org.brailleblaster.libembosser.pef.Volume;
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
		pef = new PEFTestImpl("TestPEF0002");
		pef.getMeta().setTitle("Basic default marshalling PEF");
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">"
				+ "<head><meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
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
	public void testPEFMarshalling(PEFDocument pef, String expected) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			pef.save(os);
		} catch (PEFOutputException e) {
			fail("Saving the PEF gave an error", e);
		}
		String actual = new String(os.toByteArray(), Charsets.UTF_8);
		assertEquals(actual, expected);
	}
	public static class PEFTestImpl implements PEFDocument {
		private PEFDocumentImpl delegate;
		public PEFTestImpl(String identifier) {
			this.delegate = new PEFDocumentImpl(identifier);
		}
		@Override
		public Meta getMeta() {
			return delegate.getMeta();
		}
		@Override
		public String getVersion() {
			return delegate.getVersion();
		}
		@Override
		public Volume appendnewVolume() {
			return delegate.appendnewVolume();
		}
		@Override
		public Volume insertnewVolume(int index) {
			return delegate.insertnewVolume(index);
		}
		@Override
		public Volume getVolume(int index) {
			return delegate.getVolume(index);
		}
		@Override
		public int getVolumeCount() {
			return delegate.getVolumeCount();
		}
		@Override
		public void removeVolume(int index) {
			delegate.removeVolume(index);
		}
		@Override
		public void removeVolume(Volume vol) {
			delegate.removeVolume(vol);
		}
	}
}
