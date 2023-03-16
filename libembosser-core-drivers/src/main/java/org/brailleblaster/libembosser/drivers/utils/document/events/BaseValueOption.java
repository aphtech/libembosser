/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document.events;

public abstract class BaseValueOption<T> extends BaseOption implements ValueOption<T> {
	private final T value;
	public BaseValueOption(T value) {
		this.value = value;
	}
	public T getValue() {
		return value;
	}
}