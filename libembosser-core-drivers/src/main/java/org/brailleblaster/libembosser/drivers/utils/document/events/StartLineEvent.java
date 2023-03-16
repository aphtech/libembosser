/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartLineEvent extends BaseOptionEvent<RowOption> {
	public StartLineEvent() {
		super();
	}
	public StartLineEvent(Set<RowOption> options) {
		super(options);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StartLineEvent other = (StartLineEvent)obj;
		return !optionsEquals(other);
	}
	@Override
	public int hashCode() {
		return 1;
	}
}