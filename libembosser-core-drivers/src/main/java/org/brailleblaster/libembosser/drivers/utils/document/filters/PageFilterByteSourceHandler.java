/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document.filters;

import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;

import com.google.common.io.ByteSource;

public class PageFilterByteSourceHandler implements DocumentToByteSourceHandler {
	private final DocumentToByteSourceHandler delegate;
	private final PageFilter filter;
	public PageFilterByteSourceHandler(DocumentToByteSourceHandler delegate, PageRanges pages) {
		this.delegate = delegate;
		filter = new PageFilter(pages);
	}
	public DocumentToByteSourceHandler getDelegate() {
		return delegate;
	}
	@Override
	public ByteSource asByteSource() {
		return getDelegate().asByteSource();
	}
	@Override
	public void onEvent(DocumentEvent event) {
		if (filter.retainEvent(event)) {
			delegate.onEvent(event);
		}
	}
}
