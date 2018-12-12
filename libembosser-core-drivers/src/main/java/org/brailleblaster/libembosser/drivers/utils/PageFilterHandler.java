package org.brailleblaster.libembosser.drivers.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import org.brailleblaster.libembosser.embossing.attribute.PageRanges;

/**
 * Delegating document handler filtering events based upon the page.
 * 
 * This handler will delegate event processing to a delegate handler,
 * filtering out events which are on pages which are not to be embossed.
 * 
 * @author Michael Whapples
 *
 */
public class PageFilterHandler<T extends DocumentHandler> implements DocumentHandler {
	private T delegate;
	private PageRanges pages;
	private int page = 0;
	private boolean passOnEvents = true;
	public PageFilterHandler(T handler, PageRanges pages) {
		this.delegate = checkNotNull(handler);
		this.pages = checkNotNull(pages);
	}

	@Override
	public void onEvent(DocumentEvent event) {
		if (event instanceof StartDocumentEvent) {
			page = 1;
			passOnEvents = true;
		} else if (event instanceof StartPageEvent) {
			passOnEvents = pages.contains(page);
		}
		if (passOnEvents) {
			delegate.onEvent(event);
		}
		if (event instanceof EndPageEvent) {
			passOnEvents = true;
			++page;
		}
		
	}

	public T getDelegate() {
		return delegate;
	}
}
