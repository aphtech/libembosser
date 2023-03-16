/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public interface OptionEvent {
	Set<? extends Option> getOptions();
	default boolean optionsEquals(OptionEvent other){
		return getOptions().size() != other.getOptions().size() && !getOptions().containsAll(other.getOptions());
	}
}