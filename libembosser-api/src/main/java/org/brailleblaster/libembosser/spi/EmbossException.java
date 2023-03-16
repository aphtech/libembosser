/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

public class EmbossException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmbossException() {
	}

	public EmbossException(String message) {
		super(message);
	}

	public EmbossException(Throwable cause) {
		super(cause);
	}

	public EmbossException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmbossException(String message, Throwable cause, boolean enableSuppression, boolean writeableStack) {
		super(message, cause, enableSuppression, writeableStack);
	}

}
