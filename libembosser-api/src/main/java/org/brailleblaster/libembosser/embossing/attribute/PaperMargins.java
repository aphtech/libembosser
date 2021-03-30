package org.brailleblaster.libembosser.embossing.attribute;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.print.attribute.Attribute;

import org.brailleblaster.libembosser.spi.EmbossingAttribute;
import org.brailleblaster.libembosser.spi.Margins;

public final class PaperMargins extends ObjectSyntax<Margins> implements EmbossingAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PaperMargins(Margins margins) {
		super(checkNotNull(margins));
	}

	@Override
	public Class<? extends Attribute> getCategory() {
		return this.getClass();
	}

	@Override
	public String getName() {
		return "paper-margins";
	}
	@Override
	public boolean equals(Object object) {
		return object instanceof PaperMargins && super.equals(object);
	}
}
