package org.brailleblaster.libembosser.embossing.attribute;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.print.attribute.Attribute;

import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossingAttribute;

public final class BrailleCellType extends ObjectSyntax<BrlCell> implements EmbossingAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BrailleCellType(BrlCell cell) {
		super(checkNotNull(cell));
	}
	@Override
	public Class<? extends Attribute> getCategory() {
		return this.getClass();
	}
	@Override
	public String getName() {
		return "braille-cell-type";
	}
	@Override
	public boolean equals(Object object) {
		return object instanceof BrailleCellType && super.equals(object);
	}
}
