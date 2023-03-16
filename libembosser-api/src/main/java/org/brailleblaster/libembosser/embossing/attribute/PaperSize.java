/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.embossing.attribute;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.print.attribute.Attribute;

import org.brailleblaster.libembosser.spi.EmbossingAttribute;
import org.brailleblaster.libembosser.spi.Rectangle;

public final class PaperSize extends ObjectSyntax<Rectangle> implements EmbossingAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PaperSize(Rectangle paper) {
		super(checkNotNull(paper));
	}
	@Override
	public Class<? extends Attribute> getCategory() {
		return this.getClass();
	}
	@Override
	public String getName() {
		return "paper-size";
	}
	@Override
	public boolean equals(Object object) {
		return object instanceof PaperSize && super.equals(object);
	}
}
