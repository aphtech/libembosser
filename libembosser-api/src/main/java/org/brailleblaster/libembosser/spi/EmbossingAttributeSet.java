/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

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
