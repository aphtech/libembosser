/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.embossing.attribute;

import java.util.Objects;

import org.brailleblaster.libembosser.spi.EmbossingAttribute;

public abstract class ObjectSyntax<T> implements EmbossingAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T value;
	public ObjectSyntax(T value) {
		this.value = value;
	}
	public T getValue() {
		return value;
	}
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ObjectSyntax)) {
			return false;
		}
		ObjectSyntax<?> other = (ObjectSyntax<?>) obj;
		return Objects.equals(value, other.value);
	}
}
