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
import org.brailleblaster.libembosser.spi.Layout;

public final class PaperLayout extends ObjectSyntax<Layout> implements EmbossingAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PaperLayout(Layout sides) {
		super(checkNotNull(sides));
	}
	@Override
	public Class<? extends Attribute> getCategory() {
		return this.getClass();
	}
	@Override
	public String getName() {
		return "paper-layout";
	}
	@Override
	public boolean equals(Object object) {
		return object instanceof PaperLayout && super.equals(object);
	}
}
