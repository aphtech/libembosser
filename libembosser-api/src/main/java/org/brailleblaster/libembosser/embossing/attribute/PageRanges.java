package org.brailleblaster.libembosser.embossing.attribute;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;

import org.brailleblaster.libembosser.spi.EmbossingAttribute;

public final class PageRanges extends SetOfIntegerSyntax implements EmbossingAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PageRanges(int member) {
		super(member);
	}
	public PageRanges(int[][] members) {
		super(members);
	}
	public PageRanges(int lowerBound, int upperBound) {
		super(lowerBound, upperBound);
	}
	public PageRanges(String members) {
		super(members);
	}

	@Override
	public Class<? extends Attribute> getCategory() {
		return this.getClass();
	}

	@Override
	public String getName() {
		return "page-ranges";
	}
	@Override
	public boolean equals(Object object) {
		return object instanceof PageRanges ? super.equals(object) : false;
	}

}
