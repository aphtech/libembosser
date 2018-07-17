package org.brailleblaster.libembosser.simplepef;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.PEFFactory;
import org.brailleblaster.libembosser.pef.PEFInputException;
import org.brailleblaster.libembosser.pef.PEFOutputException;
import org.brailleblaster.libembosser.pef.Page;
import org.brailleblaster.libembosser.pef.Volume;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

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
		pef.getMeta().setDate("11 July 2018");
		pef.getMeta().setDescription("An untitled PEF");
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">"
				+ "<head><meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
				+ "<dc:date>11 July 2018</dc:date>"
				+ "<dc:description>An untitled PEF</dc:description>"
				+ "<dc:format>application/x-pef+xml</dc:format>"
				+ "<dc:identifier>TestPEF0002</dc:identifier>"
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
		pef = new PEFDocumentImpl("Test0003");
		pef.getMeta().setTitle("PEF with Braille");
		pef.getMeta().setDescription("A test PEF which contains some Braille");
		Volume vol = pef.getVolume(0);
		vol.setCols(35);
		vol.setDuplex(true);
		vol.setRowGap(2);
		vol.setRows(28);
		Page page = pef.getVolume(0).getSection(0).getPage(0);
		page.appendRow("\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e");
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">"
				+ "<head><meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
				+ "<dc:description>A test PEF which contains some Braille</dc:description>"
				+ "<dc:format>application/x-pef+xml</dc:format>"
				+ "<dc:identifier>Test0003</dc:identifier>"
				+ "<dc:title>PEF with Braille</dc:title>"
				+ "</meta></head>"
				+ "<body>"
				+ "<volume cols=\"35\" duplex=\"true\" rowgap=\"2\" rows=\"28\">"
				+ "<section><page>"
				+ "<row>\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e</row>"
				+ "</page></section>"
				+ "</volume>"
				+ "</body>"
				+ "</pef>";
		data.add(new Object[] { pef, expected });
		
		pef = new PEFDocumentImpl("Test0004");
		pef.getMeta().setTitle("PEF with Braille");
		pef.getMeta().setDescription("A test PEF which contains some Braille");
		pef.getMeta().setContributors(ImmutableList.of("Michael", "Bill"));
		pef.getMeta().setCoverages(ImmutableList.of("Europe", "North America", "Africa"));
		pef.getMeta().setCreators(ImmutableList.of("Jane"));
		pef.getMeta().setLanguages(ImmutableList.of("English"));
		vol = pef.getVolume(0);
		vol.setCols(35);
		vol.setDuplex(true);
		vol.setRowGap(2);
		vol.setRows(28);
		page = pef.getVolume(0).getSection(0).getPage(0);
		page.appendRow("\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e");
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">"
				+ "<head><meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
				+ "<dc:contributor>Michael</dc:contributor>"
				+ "<dc:contributor>Bill</dc:contributor>"
				+ "<dc:coverage>Europe</dc:coverage>"
				+ "<dc:coverage>North America</dc:coverage>"
				+ "<dc:coverage>Africa</dc:coverage>"
				+ "<dc:creator>Jane</dc:creator>"
				+ "<dc:description>A test PEF which contains some Braille</dc:description>"
				+ "<dc:format>application/x-pef+xml</dc:format>"
				+ "<dc:identifier>Test0004</dc:identifier>"
				+ "<dc:language>English</dc:language>"
				+ "<dc:title>PEF with Braille</dc:title>"
				+ "</meta></head>"
				+ "<body>"
				+ "<volume cols=\"35\" duplex=\"true\" rowgap=\"2\" rows=\"28\">"
				+ "<section><page>"
				+ "<row>\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e</row>"
				+ "</page></section>"
				+ "</volume>"
				+ "</body>"
				+ "</pef>";
		data.add(new Object[] { pef, expected });
		
		pef = new PEFDocumentImpl("Test0004");
		pef.getMeta().setTitle("PEF with Braille");
		pef.getMeta().setDescription("A test PEF which contains some Braille");
		pef.getMeta().setContributors(ImmutableList.of("Michael"));
		pef.getMeta().setCoverages(ImmutableList.of("Europe"));
		pef.getMeta().setCreators(ImmutableList.of("Bill", "Jane"));
		pef.getMeta().setLanguages(ImmutableList.of("English", "French"));
		pef.getMeta().setRights(ImmutableList.of("All rights"));
		pef.getMeta().setRelations(ImmutableList.of("Another"));
		pef.getMeta().setSources(ImmutableList.of("Experience"));
		pef.getMeta().setSubjects(ImmutableList.of("Example"));
		pef.getMeta().setTypes(ImmutableList.of("Braille doc"));
		pef.getMeta().setPublishers(ImmutableList.of("Self published"));
		vol = pef.getVolume(0);
		vol.setCols(35);
		vol.setDuplex(true);
		vol.setRowGap(2);
		vol.setRows(28);
		page = pef.getVolume(0).getSection(0).getPage(0);
		page.appendRow("\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e");
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">"
				+ "<head><meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
				+ "<dc:contributor>Michael</dc:contributor>"
				+ "<dc:coverage>Europe</dc:coverage>"
				+ "<dc:creator>Bill</dc:creator>"
				+ "<dc:creator>Jane</dc:creator>"
				+ "<dc:description>A test PEF which contains some Braille</dc:description>"
				+ "<dc:format>application/x-pef+xml</dc:format>"
				+ "<dc:identifier>Test0004</dc:identifier>"
				+ "<dc:language>English</dc:language>"
				+ "<dc:language>French</dc:language>"
				+ "<dc:publisher>Self published</dc:publisher>"
				+ "<dc:relation>Another</dc:relation>"
				+ "<dc:rights>All rights</dc:rights>"
				+ "<dc:source>Experience</dc:source>"
				+ "<dc:subject>Example</dc:subject>"
				+ "<dc:title>PEF with Braille</dc:title>"
				+ "<dc:type>Braille doc</dc:type>"
				+ "</meta></head>"
				+ "<body>"
				+ "<volume cols=\"35\" duplex=\"true\" rowgap=\"2\" rows=\"28\">"
				+ "<section><page>"
				+ "<row>\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e</row>"
				+ "</page></section>"
				+ "</volume>"
				+ "</body>"
				+ "</pef>";
		data.add(new Object[] { pef, expected });
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
