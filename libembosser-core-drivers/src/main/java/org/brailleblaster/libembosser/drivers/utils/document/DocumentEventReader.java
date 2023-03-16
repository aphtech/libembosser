/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils.document;

import java.util.Iterator;

import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;

public interface DocumentEventReader extends Iterator<DocumentEvent> {

}
