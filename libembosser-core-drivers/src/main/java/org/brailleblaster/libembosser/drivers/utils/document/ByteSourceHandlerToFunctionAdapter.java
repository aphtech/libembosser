/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document;

import java.util.Iterator;
import java.util.function.Function;

import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;

import com.google.common.io.ByteSource;

public interface ByteSourceHandlerToFunctionAdapter extends DocumentToByteSourceHandler, Function<Iterator<DocumentEvent>, ByteSource> {
	default ByteSource apply(Iterator<DocumentEvent> doc) {
		while (doc.hasNext()) {
			onEvent(doc.next());
		}
		return asByteSource();
	}
}
