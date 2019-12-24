package org.brailleblaster.libembosser.drivers.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.brailleblaster.libembosser.drivers.utils.DocumentParser.ParseException;
import org.brailleblaster.libembosser.drivers.utils.document.events.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.CellsPerLine;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.Duplex;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.GraphicOption;
import org.brailleblaster.libembosser.drivers.utils.document.events.LinesPerPage;
import org.brailleblaster.libembosser.drivers.utils.document.events.Option;
import org.brailleblaster.libembosser.drivers.utils.document.events.OptionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.RowGap;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.ValueOption;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

public class DocumentParserTest {
	private void assertEqualEvents(List<DocumentEvent> expectedEvents, final List<DocumentEvent> actualEvents) {
		assertEquals(actualEvents.size(), expectedEvents.size(), "Not got the expected number of events");
		for (int i = 0; i < expectedEvents.size(); ++i) {
			DocumentEvent expectedEvent = expectedEvents.get(i);
			DocumentEvent actualEvent = actualEvents.get(i);
			// Are the events the same type
			assertEquals(actualEvent.getClass(), expectedEvent.getClass(), "Not expected event type");
			if (expectedEvent instanceof OptionEvent) {
				// Check the event has same options
				Set<? extends Option> actualOptions = ((OptionEvent)actualEvent).getOptions();
				Set<? extends Option> expectedOptions = ((OptionEvent)expectedEvent).getOptions();
				assertEquals(actualOptions.size(), expectedOptions.size(), "Not got the expected number of options on the event");
				for (Option expectedOption: expectedOptions) {
					// Check there is an option of the same type
					Optional<? extends Option> actualOption = actualOptions.stream().filter(o -> o.getClass().equals(expectedOption.getClass())).findFirst();
					assertTrue(actualOption.isPresent());
					if (expectedOption instanceof ValueOption) {
						Object expected = ((ValueOption<?>)expectedOption).getValue();
						Object actual = ((ValueOption<?>)actualOption.get()).getValue();
						if (actual instanceof BufferedImage && expected instanceof BufferedImage) {
							assertImageEquals((BufferedImage)actual, (BufferedImage)expected);
						} else {
							assertEquals(actual, expected, String.format("Values in option %s do not match", expectedOption.getClass().getCanonicalName()));
						}
					}
				}
			} else if (expectedEvent instanceof BrailleEvent) {
				assertEquals(((BrailleEvent)actualEvent).getBraille(), ((BrailleEvent)expectedEvent).getBraille(), "Braille is not expected Braille.");
			}
		}
	}
	@DataProvider(name="brfProvider")
	public Iterator<Object[]> brfProvider() {
		List<Object[]> data = new ArrayList<>();
		// Test an empty document
		final ImmutableList<DocumentEvent> minimumDocumentEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new ByteArrayInputStream(new byte[0]), minimumDocumentEvents});
		// A blank page potentially may be just the formfeed 
		data.add(new Object[] {new ByteArrayInputStream(new byte[] {(byte)'\f'}), minimumDocumentEvents});
		// Sometimes a blank page may just contain blank lines
		data.add(new Object[] {new ByteArrayInputStream(Strings.repeat("\r\n", 24).concat("\f").getBytes(Charsets.US_ASCII)), minimumDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream(Strings.repeat("\n", 24).concat("\f").getBytes(Charsets.US_ASCII)), minimumDocumentEvents});
		// Braille character bytes should be copied to output
		final ImmutableList<DocumentEvent> basicDocumentEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("TEST Braille"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new ByteArrayInputStream("TEST Braille".getBytes(Charsets.US_ASCII)), basicDocumentEvents});
		// Lines of characters should be separated by the correct start and end line events.
		final ImmutableList<DocumentEvent> multiLineDocumentEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("TEST Braille"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("SECOND L9E4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new ByteArrayInputStream("TEST Braille\r\nSECOND L9E4".getBytes(Charsets.US_ASCII)), multiLineDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream("TEST Braille\nSECOND L9E4".getBytes(Charsets.US_ASCII)), multiLineDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream("TEST Braille\nSECOND L9E4".concat(Strings.repeat("\n", 23)).getBytes(Charsets.US_ASCII)), multiLineDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream(String.format("TEST Braille%sSECOND L9E4%s%s", "\n", Strings.repeat("\n", 23), "\f").getBytes(Charsets.US_ASCII)), multiLineDocumentEvents});
		// Test blank lines between lines of Braille.
		final ImmutableList<DocumentEvent> blankLinesDocumentEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("TEST Braille"), new EndLineEvent(), new StartLineEvent(), new EndLineEvent(), new StartLineEvent(), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("SECOND L9E4"), new EndLineEvent(), new StartLineEvent(), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("?IRD L9E4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new ByteArrayInputStream("TEST Braille\n\n\nSECOND L9E4\r\n\r\n?IRD L9E4".getBytes(Charsets.US_ASCII)), blankLinesDocumentEvents});
		// Multipage tests
		final ImmutableList<DocumentEvent> multiPageDocumentEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("TEST Braille"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("SECOND PAGE4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new ByteArrayInputStream("TEST Braille\fSECOND PAGE4".getBytes(Charsets.US_ASCII)), multiPageDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream(String.format("TEST Braille%s\fSECOND PAGE4", Strings.repeat("\n", 24)).getBytes(Charsets.US_ASCII)), multiPageDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream(String.format("TEST Braille%s\fSECOND PAGE4", Strings.repeat("\r\n", 24)).getBytes(Charsets.US_ASCII)), multiPageDocumentEvents});
		// Blank page test.
		final ImmutableList<DocumentEvent> blankPageDocumentEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("TEST Braille"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("SECOND PAGE4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new ByteArrayInputStream("TEST Braille\f\fSECOND PAGE4".getBytes(Charsets.US_ASCII)), blankPageDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream(String.format("TEST Braille%s\f\fSECOND PAGE4", Strings.repeat("\n", 24)).getBytes(Charsets.US_ASCII)), blankPageDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream(String.format("TEST Braille%s\f\fSECOND PAGE4", Strings.repeat("\r\n", 24)).getBytes(Charsets.US_ASCII)), blankPageDocumentEvents});
		data.add(new Object[] {new ByteArrayInputStream(String.format("TEST Braille%s\f%s\fSECOND PAGE4", Strings.repeat("\n", 24), Strings.repeat("\n", 24)).getBytes(Charsets.US_ASCII)), blankPageDocumentEvents});
		return data.iterator();
	}
	@Test(dataProvider="brfProvider")
	public void testParseBrf(InputStream input, List<DocumentEvent> expectedEvents) {
		DocumentParser parser = new DocumentParser();
		final List<DocumentEvent> actualEvents = new ArrayList<>();
		try {
			parser.parseBrf(input, e -> actualEvents.add(e));
		} catch (ParseException e) {
			fail("Problem parsing the BRF", e);
		}
		assertEqualEvents(expectedEvents, actualEvents);
	}
	
	@DataProvider(name="pefProvider")
	public Iterator<Object[]> pefProvider() throws IOException {
		List<Object[]> data = new ArrayList<>();
		ImmutableList<DocumentEvent> expectedEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new Duplex(false), new RowGap(0), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		ByteSource input = Resources.asByteSource(Resources.getResource(this.getClass(), "minimal.pef"));
		data.add(new Object[] {input, expectedEvents});
		expectedEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new Duplex(false), new RowGap(0), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u281e\u2811\u280c\u2800\u2819\u2815\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		input = Resources.asByteSource(Resources.getResource(this.getClass(), "basic_document.pef"));
		data.add(new Object[] {input, expectedEvents});
		expectedEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new Duplex(false), new RowGap(2), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u281e\u2811\u280c\u2800\u2819\u2815\u2809"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2800\u280e\u2811\u2809\u2815\u281d\u2819\u2800\u2807\u2814\u2811"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		input = Resources.asByteSource(Resources.getResource(this.getClass(), "multi_line.pef"));
		data.add(new Object[] {input, expectedEvents});
		expectedEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new Duplex(false), new RowGap(0), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u281e\u2811\u280c\u2800\u2819\u2815\u2809"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2800\u280e\u2811\u2809\u2815\u281d\u2819\u2800\u2807\u2814\u2811"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u281d\u2815\u282e\u2817\u2800\u280f\u2801\u281b\u2811"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		input = Resources.asByteSource(Resources.getResource(this.getClass(), "multi_page.pef"));
		data.add(new Object[] {input, expectedEvents});
		expectedEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new Duplex(false), new RowGap(0), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u281e\u2811\u280c\u2800\u2819\u2815\u2809"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2800\u280e\u2811\u2809\u2815\u281d\u2819\u2800\u2807\u2814\u2811"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u281d\u2815\u282e\u2817\u2800\u280f\u2801\u281b\u2811"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		input = Resources.asByteSource(Resources.getResource(this.getClass(), "multi_section.pef"));
		data.add(new Object[] {input, expectedEvents});
		expectedEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new Duplex(false), new RowGap(0), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u281e\u2811\u280c\u2800\u2819\u2815\u2809"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2800\u280e\u2811\u2809\u2815\u281d\u2819\u2800\u2807\u2814\u2811"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(38), new Duplex(true), new RowGap(0), new LinesPerPage(24))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u281d\u2815\u282e\u2817\u2800\u280f\u2801\u281b\u2811"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		input = Resources.asByteSource(Resources.getResource(this.getClass(), "multi_volume.pef"));
		data.add(new Object[] {input, expectedEvents});
		expectedEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new LinesPerPage(25), new Duplex(true), new RowGap(0))), new StartSectionEvent(ImmutableSet.of(new CellsPerLine(37), new LinesPerPage(24), new Duplex(false), new RowGap(1))), new StartPageEvent(ImmutableSet.of(new CellsPerLine(35), new LinesPerPage(22), new RowGap(3))), new StartLineEvent(ImmutableSet.of(new RowGap(4))), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		input = Resources.asByteSource(Resources.getResource(this.getClass(), "options.pef"));
		data.add(new Object[] {input, expectedEvents});
		BufferedImage img = ImageIO.read(this.getClass().getResourceAsStream("APH_Logo.png"));
		expectedEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new LinesPerPage(25), new Duplex(false), new RowGap(0))), new StartSectionEvent(), new StartPageEvent(), new StartGraphicEvent(), new StartLineEvent(), new BrailleEvent("\u2801\u2803"), new EndLineEvent(), new EndGraphicEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img), new GraphicOption.Indent(1), new GraphicOption.Width(33), new GraphicOption.Height(10))), new EndGraphicEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		input = Resources.asByteSource(Resources.getResource(this.getClass(), "graphics.pef"));
		data.add(new Object[] {input, expectedEvents});
		return data.iterator();
	}
	
	@Test(dataProvider="pefProvider")
	public void testParsePefDom(ByteSource input, List<DocumentEvent> expectedEvents) {
		DocumentParser parser = new DocumentParser();
		final List<DocumentEvent> actualEvents = new ArrayList<>();
		try(InputStream is = input.openStream()) {
			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document inputDoc = docBuilder.parse(is);
			parser.parsePef(inputDoc, e -> actualEvents.add(e));
		} catch (IOException e) {
			fail("Problem reading the PEF", e);
		} catch (SAXException e) {
			fail("Problem parsing XML", e);
		} catch (ParserConfigurationException e) {
			fail("Problem creating XML parser", e);
		}
		assertEqualEvents(expectedEvents, actualEvents);
	}
	public static void assertImageEquals(BufferedImage imageA, BufferedImage imageB) {
		if (imageA.getWidth() != imageB.getWidth()) {
			fail(String.format("Image widths do not match, expected %d but got %d", imageB.getWidth(), imageB.getWidth()));
		}
		if (imageA.getHeight() != imageB.getHeight()) {
			fail(String.format("Image heights do not match, expected %d but got %d", imageB.getHeight(), imageB.getHeight()));
		}
		int width = imageA.getWidth();
		int height = imageA.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (imageA.getRGB(x, y) != imageB.getRGB(x, y)) {
					fail(String.format("Pixel %d,%d do not match, expected %d but found %d", x, y, imageB.getRGB(x, y), imageA.getRGB(x, y)));
				}
			}
		}
	}
}
