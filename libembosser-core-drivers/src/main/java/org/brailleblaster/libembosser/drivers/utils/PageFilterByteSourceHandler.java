package org.brailleblaster.libembosser.drivers.utils;

import org.brailleblaster.libembosser.embossing.attribute.PageRanges;

import com.google.common.io.ByteSource;

public class PageFilterByteSourceHandler extends PageFilterHandler<DocumentToByteSourceHandler> implements DocumentToByteSourceHandler {

	public PageFilterByteSourceHandler(DocumentToByteSourceHandler delegate, PageRanges pages) {
		super(delegate, pages);
	}
	@Override
	public ByteSource asByteSource() {
		return getDelegate().asByteSource();
	}

}
