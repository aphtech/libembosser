/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document.filters;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brailleblaster.libembosser.drivers.utils.document.events.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartVolumeEvent;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class PageFilterTest {
	private Object[] createPageFilterDataEntry(String[][][][] inputBraille, PageRanges pages) {
		int pageCounter = 0;
		List<DocumentEvent> inputEvents = new ArrayList<>();
		List<DocumentEvent> expectedEvents = new ArrayList<>();
		DocumentEvent curEvent = new StartDocumentEvent();
		inputEvents.add(curEvent);
		expectedEvents.add(curEvent);
		for (String[][][] vol: inputBraille) {
			curEvent = new StartVolumeEvent();
			inputEvents.add(curEvent);
			expectedEvents.add(curEvent);
			for (String[][] section: vol) {
				curEvent = new StartSectionEvent();
				inputEvents.add(curEvent);
				expectedEvents.add(curEvent);
				for (String[] page: section) {
					++pageCounter;
					curEvent = new StartPageEvent();
					inputEvents.add(curEvent);
					if (pages.contains(pageCounter)) {
						expectedEvents.add(curEvent);
					}
					for (String row: page) {
						curEvent = new StartLineEvent();
						inputEvents.add(curEvent);
						if (pages.contains(pageCounter)) {
							expectedEvents.add(curEvent);
						}
						curEvent = new BrailleEvent(row);
						inputEvents.add(curEvent);
						if (pages.contains(pageCounter)) {
							expectedEvents.add(curEvent);
						}
						curEvent = new EndLineEvent();
						inputEvents.add(curEvent);
						if (pages.contains(pageCounter)) {
							expectedEvents.add(curEvent);
						}
					}
					curEvent = new EndPageEvent();
					inputEvents.add(curEvent);
					if (pages.contains(pageCounter)) {
						expectedEvents.add(curEvent);
					}
				}
				curEvent = new EndSectionEvent();
				inputEvents.add(curEvent);
				expectedEvents.add(curEvent);
			}
			curEvent = new EndVolumeEvent();
			inputEvents.add(curEvent);
			expectedEvents.add(curEvent);
		}
		curEvent = new EndDocumentEvent();
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
		
		PageFilter filter = new PageFilter(pages);
		List<DocumentEvent> actualEvents = ImmutableList.copyOf(filter.apply(inputEvents.iterator()));
		assertEquals(actualEvents.size(), expectedEvents.size(), "Not got the expected number of events.");
		for (int i = 0; i < actualEvents.size(); ++i) {
			assertSame(actualEvents.get(i), expectedEvents.get(i), String.format("Events are not the same at index %d", i));
		}
	}
}
