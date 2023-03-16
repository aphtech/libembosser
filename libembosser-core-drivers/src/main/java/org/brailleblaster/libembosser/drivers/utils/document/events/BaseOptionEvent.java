/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public abstract class BaseOptionEvent<T extends Option> implements OptionEvent, DocumentEvent {
	private Set<T> options;
	protected BaseOptionEvent() {
		this.options = ImmutableSet.of();
	}
	protected BaseOptionEvent(Set<T> options) {
		this.options = ImmutableSet.copyOf(options);
	}
	@Override
	public Set<T> getOptions() {
		return options;
	}
}