package org.brailleblaster.libembosser.drivers.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.DocumentEvent;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PageFilterHandlerTest {
	private Object[] createPageFilterDataEntry(String[][][][] inputBraille, PageRanges pages) {
		int pageCounter = 0;
		List<DocumentEvent> inputEvents = new ArrayList<>();
		List<DocumentEvent> expectedEvents = new ArrayList<>();
		DocumentEvent curEvent = new DocumentHandler.StartDocumentEvent();
		inputEvents.add(curEvent);
		expectedEvents.add(curEvent);
		for (String[][][] vol: inputBraille) {
			curEvent = new DocumentHandler.StartVolumeEvent();
			inputEvents.add(curEvent);
			expectedEvents.add(curEvent);
			for (String[][] section: vol) {
				curEvent = new DocumentHandler.StartSectionEvent();
				inputEvents.add(curEvent);
				expectedEvents.add(curEvent);
				for (String[] page: section) {
					++pageCounter;
					curEvent = new DocumentHandler.StartPageEvent();
					inputEvents.add(curEvent);
					if (pages.contains(pageCounter)) {
						expectedEvents.add(curEvent);
					}
					for (String row: page) {
						curEvent = new DocumentHandler.StartLineEvent();
						inputEvents.add(curEvent);
						if (pages.contains(pageCounter)) {
							expectedEvents.add(curEvent);
						}
						curEvent = new DocumentHandler.BrailleEvent(row);
						inputEvents.add(curEvent);
						if (pages.contains(pageCounter)) {
							expectedEvents.add(curEvent);
						}
						curEvent = new DocumentHandler.EndLineEvent();
						inputEvents.add(curEvent);
						if (pages.contains(pageCounter)) {
							expectedEvents.add(curEvent);
						}
					}
					curEvent = new DocumentHandler.EndPageEvent();
					inputEvents.add(curEvent);
					if (pages.contains(pageCounter)) {
						expectedEvents.add(curEvent);
					}
				}
				curEvent = new DocumentHandler.EndSectionEvent();
				inputEvents.add(curEvent);
				expectedEvents.add(curEvent);
			}
			curEvent = new DocumentHandler.EndVolumeEvent();
			inputEvents.add(curEvent);
			expectedEvents.add(curEvent);
		}
		curEvent = new DocumentHandler.EndDocumentEvent();
		inputEvents.add(curEvent);
		expectedEvents.add(curEvent);
		return new Object[] {inputEvents, pages, expectedEvents};
	}
	@DataProvider(name="filteredPagesProvider")
	public Iterator<Object[]> filteredPagesProvider() {
		List<Object[]> data = new ArrayList<>();
		String[][][][] inputBraille = new String[][][][] {{{{"\u280f\u2801\u281b\u2811\u2800\u283c\u2801"}}, {{"\u280f\u2801\u281b\u2811\u2800\u283c\u2803"}, {"\u280f\u2801\u281b\u2811\u2800\u283c\u2809"}}}, {{{"\u280f\u2801\u281b\u2811\u283c\u2801"}, {"\u280f\u2801\u281b\u2811\u283c\u2803"}, {"\u280f\u2801\u281b\u2811\u283c\u2809"}}}};
		PageRanges pages = new PageRanges();
		data.add(createPageFilterDataEntry(inputBraille, pages));
		pages = new PageRanges(1);
		data.add(createPageFilterDataEntry(inputBraille, pages));
		pages = new PageRanges(3);
		data.add(createPageFilterDataEntry(inputBraille, pages));
		pages = new PageRanges(6);
		data.add(createPageFilterDataEntry(inputBraille, pages));
		pages = new PageRanges(1, 4);
		data.add(createPageFilterDataEntry(inputBraille, pages));
		pages = new PageRanges(2, 5);
		data.add(createPageFilterDataEntry(inputBraille, pages));
		return data.iterator();
	}
	@Test(dataProvider="filteredPagesProvider")
	public void testPagesFilter(List<DocumentEvent> inputEvents, PageRanges pages, List<DocumentEvent> expectedEvents) {
		List<DocumentEvent> actualEvents = new ArrayList<>();
		PageFilterHandler<DocumentHandler> filter = new PageFilterHandler<DocumentHandler>(e -> actualEvents.add(e), pages);
		for (DocumentEvent event: inputEvents) {
			filter.onEvent(event);
		}
		assertEquals(actualEvents.size(), expectedEvents.size(), "Not got the expected number of events.");
		for (int i = 0; i < actualEvents.size(); ++i) {
			assertSame(actualEvents.get(i), expectedEvents.get(i), String.format("Events are not the same at index %d", i));
		}
	}
}
