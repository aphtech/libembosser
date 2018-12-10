package org.brailleblaster.libembosser.spi;

import javax.print.attribute.HashAttributeSet;

public class EmbossingAttributeSet extends HashAttributeSet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EmbossingAttributeSet() {
		super(EmbossingAttribute.class);
	}
	public EmbossingAttributeSet(EmbossingAttribute attribute) {
		super(attribute, EmbossingAttribute.class);
	}
	public EmbossingAttributeSet(EmbossingAttribute[] attribute) {
		super(attribute, EmbossingAttribute.class);
	}
	public EmbossingAttributeSet(EmbossingAttributeSet attributes) {
		super(attributes, EmbossingAttribute.class);
	}
}
