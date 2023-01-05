package org.brailleblaster.libembosser.cli.shell;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ShellHelper {
	private Terminal terminal;
	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "Not able to copy Terminal instances."
	)
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
