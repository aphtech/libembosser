package org.brailleblaster.libembosser.cli.shell;

import java.util.List;

import org.jline.reader.LineReader;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class InputReader {
	private LineReader lineReader;
	private ShellHelper shellHelper;
	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "Not able to copy LineReader instances"
	)
	public InputReader(LineReader lineReader, ShellHelper shellHelper) {
		this.lineReader = lineReader;
		this.shellHelper = shellHelper;
	}
	public String prompt(String prompt, String defaultValue) {
		String answer = lineReader.readLine(prompt);
		if (Strings.nullToEmpty(answer).isEmpty()) {
			return defaultValue;
		}
		return answer;
	}
	public int selectFromList(String header, String prompt, List<String> options, int defaultSelection) {
		shellHelper.println(header);
		Integer selectedOption = null;
		do {
			int optionIndex = 0;
			for (String option : options) {
				shellHelper.println(String.format("%d - %s", optionIndex, option));
				optionIndex++;
			}
			String answer = lineReader.readLine(prompt);
			selectedOption = Ints.tryParse(answer);
		} while (selectedOption == null);
		return selectedOption;
	}
}
