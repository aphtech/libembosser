/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document.events;

public abstract class BaseOption {
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		return getClass().equals(other.getClass());
	}
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}