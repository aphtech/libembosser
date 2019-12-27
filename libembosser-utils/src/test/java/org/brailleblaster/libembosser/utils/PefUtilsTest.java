package org.brailleblaster.libembosser.utils;

import static org.testng.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xmlunit.assertj.XmlAssert;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

public class PefUtilsTest {
	@DataProvider(name="brfProvider")
	public Iterator<Object[]> brfProvider() {
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/minimal_40x25.xml", "/org/brailleblaster/libembosser/utils/docs/blank.brf", "MinimalDoc", 40, 25, false});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/minimal_40x25_interpoint.xml", "/org/brailleblaster/libembosser/utils/docs/blank.brf", "MinimalDocInterpoint", 40, 25, true});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/minimal_30x26.xml", "/org/brailleblaster/libembosser/utils/docs/blank.brf", "MinimalDoc", 30, 26, false});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/minimal_30x26_interpoint.xml", "/org/brailleblaster/libembosser/utils/docs/blank.brf", "MinimalDocInterpoint", 30, 26, true});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/single_page_40x25.xml", "/org/brailleblaster/libembosser/utils/docs/single_page_40x25.brf", "SinglePage", 40, 25, false});
		data.add(new Object[] { "/org/brailleblaster/libembosser/utils/docs/multiple_pages_40x25.xml", "/org/brailleblaster/libembosser/utils/docs/multiple_pages_40x25.brf", "MultiplePages", 40, 25, false});
		return data.iterator();
	}

	@Test(dataProvider="brfProvider")
	public void testFromBrfInputStream(String pefResource, String brfResource, String id, int cells, int lines, boolean duplex) {
		try(InputStream expected = getClass().getResourceAsStream(pefResource); InputStream brf = getClass().getResourceAsStream(brfResource)) {
			Document actual = PefUtils.fromBrf(brf, id, cells, lines, duplex);
			XmlAssert.assertThat(actual).and(expected).normalizeWhitespace().areIdentical();
		} catch (ParserConfigurationException | IOException e) {
			fail("Problem with XML parser");
		}
		
		
	}
	@Test(dataProvider="brfProvider")
	public void testFromBrfReader(String pefResource, String brfResource, String id, int cells, int lines, boolean duplex) {
		CharSource brfSource = Resources.asCharSource(getClass().getResource(brfResource), Charsets.US_ASCII);
		try(Reader brf = brfSource.openBufferedStream(); InputStream expected = getClass().getResourceAsStream(pefResource)) {
			Document actual = PefUtils.fromBrf(brf, id, cells, lines, duplex);
			XmlAssert.assertThat(actual).and(expected).normalizeWhitespace().areIdentical();
		} catch (ParserConfigurationException | IOException e) {
			fail("Problem with XML parser");
		}
		
	}
}
