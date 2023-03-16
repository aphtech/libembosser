/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.function.Function;

import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartPageEvent;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;

import com.google.common.collect.Iterators;

/**
 * Filter document events based upon pages.
 * 
 * This transform will keep the events on the pages to be embossed whilst filtering out those which are not to be embossed. 
 * 
 * @author Michael Whapples
 *
 */
public class PageFilter implements Function<Iterator<DocumentEvent>, Iterator<DocumentEvent>> {
	private PageRanges pages;
	private int page = 0;
	private boolean passOnEvents = true;
	public PageFilter(PageRanges pages) {
		this.pages = checkNotNull(pages);
	}

	boolean retainEvent(DocumentEvent event) {
		if (event instanceof StartDocumentEvent) {
			page = 1;
			passOnEvents = true;
		} else if (event instanceof StartPageEvent) {
			passOnEvents = pages.contains(page);
		}
		boolean result = passOnEvents;
		if (event instanceof EndPageEvent) {
			passOnEvents = true;
			++page;
		}
		return result;
	}
	@Override
	public Iterator<DocumentEvent> apply(Iterator<DocumentEvent> doc) {
		return Iterators.filter(doc, this::retainEvent);
	}
}
