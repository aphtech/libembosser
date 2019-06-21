package org.brailleblaster.libembosser.cli.shell;

import java.util.List;

import org.jline.reader.LineReader;
import org.springframework.util.StringUtils;

import com.google.common.primitives.Ints;

public class InputReader {
	private LineReader lineReader;
	private ShellHelper shellHelper;
	public InputReader(LineReader lineReader, ShellHelper shellHelper) {
		this.lineReader = lineReader;
		this.shellHelper = shellHelper;
	}
	public String prompt(String prompt, String defaultValue) {
		String answer = lineReader.readLine(prompt);
		if (StringUtils.isEmpty(answer)) {
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
		return selectedOption.intValue();
	}
}
