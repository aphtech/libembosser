/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.cli.shell;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class ShellHelper {
	private Terminal terminal;
	public ShellHelper(Terminal terminal) {
		this.terminal = terminal;
	}
	public String getColoured(String message, int colour) {
		return new AttributedStringBuilder().append(message, AttributedStyle.DEFAULT.foreground(colour)).toAnsi();
	}
	public void println(String message) {
		terminal.writer().println(message);
		terminal.flush();
	}
	public void println(String message, int colour) {
		String toPrint = getColoured(message, colour);
		terminal.writer().println(toPrint);
		terminal.flush();
	}
}
