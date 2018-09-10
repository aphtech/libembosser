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
		String expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">\n"
				+ " <head>\n  <meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
				+ "   <dc:format>application/x-pef+xml</dc:format>\n"
				+ "   <dc:identifier>TestPEF0001</dc:identifier>\n"
				+ "   <dc:title>Basic test PEF document</dc:title>\n"
				+ "  </meta>\n </head>\n"
				+ " <body>\n"
				+ "  <volume cols=\"1\" duplex=\"false\" rowgap=\"0\" rows=\"1\">\n"
				+ "   <section>\n"
				+ "    <page></page>\n"
				+ "   </section>\n"
				+ "  </volume>\n"
				+ " </body>\n"
				+ "</pef>";
		data.add(new Object[] {pef, expected});
		pef = new PEFDocumentImpl("TestPEF0002");
		pef.getMeta().setDate("11 July 2018");
		pef.getMeta().setDescription("An untitled PEF");
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">\n"
				+ " <head>\n  <meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
				+ "   <dc:date>11 July 2018</dc:date>\n"
				+ "   <dc:description>An untitled PEF</dc:description>\n"
				+ "   <dc:format>application/x-pef+xml</dc:format>\n"
				+ "   <dc:identifier>TestPEF0002</dc:identifier>\n"
				+ "  </meta>\n </head>\n"
				+ " <body>\n"
				+ "  <volume cols=\"1\" duplex=\"false\" rowgap=\"0\" rows=\"1\">\n"
				+ "   <section>\n"
				+ "    <page></page>\n"
				+ "   </section>\n"
				+ "  </volume>\n"
				+ " </body>\n"
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
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">\n"
				+ " <head>\n  <meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
				+ "   <dc:description>A test PEF which contains some Braille</dc:description>\n"
				+ "   <dc:format>application/x-pef+xml</dc:format>\n"
				+ "   <dc:identifier>Test0003</dc:identifier>\n"
				+ "   <dc:title>PEF with Braille</dc:title>\n"
				+ "  </meta>\n </head>\n"
				+ " <body>\n"
				+ "  <volume cols=\"35\" duplex=\"true\" rowgap=\"2\" rows=\"28\">\n"
				+ "   <section>\n    <page>\n"
				+ "     <row>\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e</row>\n"
				+ "    </page>\n   </section>\n"
				+ "  </volume>\n"
				+ " </body>\n"
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
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">\n"
				+ " <head>\n  <meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
				+ "   <dc:contributor>Michael</dc:contributor>\n"
				+ "   <dc:contributor>Bill</dc:contributor>\n"
				+ "   <dc:coverage>Europe</dc:coverage>\n"
				+ "   <dc:coverage>North America</dc:coverage>\n"
				+ "   <dc:coverage>Africa</dc:coverage>\n"
				+ "   <dc:creator>Jane</dc:creator>\n"
				+ "   <dc:description>A test PEF which contains some Braille</dc:description>\n"
				+ "   <dc:format>application/x-pef+xml</dc:format>\n"
				+ "   <dc:identifier>Test0004</dc:identifier>\n"
				+ "   <dc:language>English</dc:language>\n"
				+ "   <dc:title>PEF with Braille</dc:title>\n"
				+ "  </meta>\n </head>\n"
				+ " <body>\n"
				+ "  <volume cols=\"35\" duplex=\"true\" rowgap=\"2\" rows=\"28\">\n"
				+ "   <section>\n    <page>\n"
				+ "     <row>\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e</row>\n"
				+ "    </page>\n   </section>\n"
				+ "  </volume>\n"
				+ " </body>\n"
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
		expected = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\" version=\"2008-1\">\n"
				+ " <head>\n  <meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
				+ "   <dc:contributor>Michael</dc:contributor>\n"
				+ "   <dc:coverage>Europe</dc:coverage>\n"
				+ "   <dc:creator>Bill</dc:creator>\n"
				+ "   <dc:creator>Jane</dc:creator>\n"
				+ "   <dc:description>A test PEF which contains some Braille</dc:description>\n"
				+ "   <dc:format>application/x-pef+xml</dc:format>\n"
				+ "   <dc:identifier>Test0004</dc:identifier>\n"
				+ "   <dc:language>English</dc:language>\n"
				+ "   <dc:language>French</dc:language>\n"
				+ "   <dc:publisher>Self published</dc:publisher>\n"
				+ "   <dc:relation>Another</dc:relation>\n"
				+ "   <dc:rights>All rights</dc:rights>\n"
				+ "   <dc:source>Experience</dc:source>\n"
				+ "   <dc:subject>Example</dc:subject>\n"
				+ "   <dc:title>PEF with Braille</dc:title>\n"
				+ "   <dc:type>Braille doc</dc:type>\n"
				+ "  </meta>\n </head>\n"
				+ " <body>\n"
				+ "  <volume cols=\"35\" duplex=\"true\" rowgap=\"2\" rows=\"28\">\n"
				+ "   <section>\n    <page>\n"
				+ "     <row>\u2803\u2817\u2807\u2800\u2812\u281e\u2822\u281e</row>\n"
				+ "    </page>\n   </section>\n"
				+ "  </volume>\n"
				+ " </body>\n"
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
