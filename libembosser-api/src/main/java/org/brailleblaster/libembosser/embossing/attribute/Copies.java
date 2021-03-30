package org.brailleblaster.libembosser.embossing.attribute;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;

import org.brailleblaster.libembosser.spi.EmbossingAttribute;

public final class Copies extends IntegerSyntax implements EmbossingAttribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Copies(int value) {
		super(value, 1, Integer.MAX_VALUE);
	}
	@Override
	public Class<? extends Attribute> getCategory() {
		return this.getClass();
	}

	@Override
	public String getName() {
		return "copies";
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Copies && super.equals(obj);
	}
}
