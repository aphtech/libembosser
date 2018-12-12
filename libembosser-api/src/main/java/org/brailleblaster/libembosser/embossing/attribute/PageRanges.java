package org.brailleblaster.libembosser.embossing.attribute;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Arrays;
import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;

import org.brailleblaster.libembosser.spi.EmbossingAttribute;

public final class PageRanges extends SetOfIntegerSyntax implements EmbossingAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static boolean checkIsPositiveInteger(String value) {
		int result = 0;
		try {
			result = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return result > 0;
	}

	public PageRanges() {
		this(1, Integer.MAX_VALUE);
	}
	public PageRanges(int member) {
		super(member);
		checkArgument(member > 0);
	}
	public PageRanges(int[][] members) {
		super(members);
		checkArgument(Arrays.stream(members).flatMapToInt(Arrays::stream).allMatch(v -> v > 0));
	}
	public PageRanges(int lowerBound, int upperBound) {
		super(lowerBound, upperBound);
		checkArgument(lowerBound > 0 && upperBound > 0);
	}
	public PageRanges(String members) {
		super(members);
		checkArgument(Arrays.stream(members.split("[,:-]")).map(String::trim).allMatch(PageRanges::checkIsPositiveInteger));
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
