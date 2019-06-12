package org.brailleblaster.libembosser.drivers.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.expectThrows;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.ImageOption;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartVolumeEvent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class DocumentToPrintableHandlerTest {
	private DocumentToPrintableHandler.Builder createHandlerBuilder() {
		return new DocumentToPrintableHandler.Builder();
	}
	@DataProvider(name="pageProvider")
	public Iterator<Object[]> pageProvider() {
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), 1});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), 3});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), 2});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), 3});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), 3});
		// Check duplex volumes always start on right hand pages.
		data.add(new Object[] {createHandlerBuilder().setDuplex(true).build(), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), 4});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), 3});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), 4});
		return data.iterator();
	}
	@Test(dataProvider="pageProvider")
	public void testPageCount(DocumentToPrintableHandler handler, List<DocumentEvent> events, int pages) {
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		assertEquals(handler.getpageCount(), pages);
	}
	@DataProvider(name="rowProvider")
	public Iterator<Object[]> rowProvider() {
		List<Object[]> data = new ArrayList<>();
		String row1 = "\u2801\u2803\u2800\u2811";
		String row2 = "\u2811\u2800\u2803\u2800\u2801";
		ImmutableList<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(row1), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(row2), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		ImmutableList<DocumentToPrintableHandler.Page> pages = ImmutableList.of(new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row(row1), new DocumentToPrintableHandler.Row(row2)));
		data.add(new Object[] {events, pages});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(row1), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(row2), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		pages = ImmutableList.of(new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row(row1)), new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row(row2)));
		data.add(new Object[] {events, pages});
		// Test alternative Braille for a graphic is used when a graphic has no image
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(row1), new EndLineEvent(), new StartGraphicEvent(), new StartLineEvent(), new BrailleEvent(row2), new EndLineEvent(), new EndGraphicEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		pages = ImmutableList.of(new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row(row1), new DocumentToPrintableHandler.Row(row2)));
		data.add(new Object[] {events, pages});
		// Test alternative Braille is not included when graphic has image
		Image image1 = ImageIO.read(getClass().getResourceAsStream("/org/brailleblaster/libembosser/drivers/utils/APH_Logo.png"));
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(row1), new EndLineEvent(), new StartGraphicEvent(ImmutableSet.of(new ImageOption(image1))), new StartLineEvent(), new BrailleEvent(row2), new EndLineEvent(), new EndGraphicEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		pages = ImmutableList.of(new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row(row1), new DocumentToPrintableHandler.Graphic(image1)));
		data.add(new Object[] {events, pages});
		return data.iterator();
	}
	@Test(dataProvider="rowProvider")
	public void testAddingRows(List<DocumentEvent> events, List<DocumentToPrintableHandler.Page>pages ) {
		DocumentToPrintableHandler handler = createHandlerBuilder().build();
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		assertEquals(handler.getpageCount(), pages.size());
		for (int i = 0; i < handler.getpageCount(); ++i) {
			DocumentToPrintableHandler.Page actualPage = handler.getPage(i);
			DocumentToPrintableHandler.Page expectedPage = pages.get(i);
			assertEquals(actualPage, expectedPage);
		}
	}
	@DataProvider(name="invalidStateChangeProvider")
	public Iterator<Object[]> invalidStateChangeProvider() {
		List<Object[]> data = new ArrayList<>();
		// Check state for no previous events
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new StartLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new BrailleEvent("Test text")});
		// Tests for at document level
		ImmutableList<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("test text")});
		// Test for in a volume
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("some text")});
		// Tests for section level
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("More text")});
		// Tests for page level
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("Some text")});
		// Tests for line level
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartLineEvent()});
		// Tests for graphic level
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartGraphicEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartGraphicEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("Some text")});
		return data.iterator();
	}
	@Test(dataProvider="invalidStateChangeProvider")
	public void testInvalidStateChanges(DocumentHandler handler, List<DocumentEvent> events, DocumentEvent errorEvent) {
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		expectThrows(IllegalStateException.class, () -> handler.onEvent(errorEvent));
	}
	@DataProvider(name="pageEqualityProvider")
	public Object[][] pageEqualityProvider() {
		return new Object[][] {
			{new DocumentToPrintableHandler.Page(), new DocumentToPrintableHandler.Page(), true},
			{new DocumentToPrintableHandler.Page(), new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2801")), false},
			{new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2801")), new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2801")), true},
			{new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2803")), new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2801")), false},
			{new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2811")), new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2811")), true},
			{new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2801"), new DocumentToPrintableHandler.Row("\u2811")), new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2801"), new DocumentToPrintableHandler.Row("\u2811")), true},
			{new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2801"), new DocumentToPrintableHandler.Row("\u2811")), new DocumentToPrintableHandler.Page(new DocumentToPrintableHandler.Row("\u2811"), new DocumentToPrintableHandler.Row("\u2801")), false},
		};
	}
	@Test(dataProvider="pageEqualityProvider")
	public void testPageEquality(DocumentToPrintableHandler.Page actualPage, DocumentToPrintableHandler.Page expectedPage, boolean equal) {
		if (equal) {
			assertEquals(actualPage, expectedPage);
		} else {
			assertNotEquals(actualPage, expectedPage);
		}
	}
}
