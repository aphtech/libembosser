/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.cli.config;

import org.brailleblaster.libembosser.EmbosserService;
import org.brailleblaster.libembosser.cli.shell.InputReader;
import org.brailleblaster.libembosser.cli.shell.ShellHelper;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class SpringShellConfiguration {
	@Bean
	public ShellHelper shellHelper(@Lazy Terminal terminal) {
		return new ShellHelper(terminal);
	}
	@Bean
	public InputReader inputReader(@Lazy LineReader lineReader, ShellHelper shell) {
		return new InputReader(lineReader, shell);
	}
	@Bean
	public EmbosserService embosserService() {
		return EmbosserService.getInstance();
	}
}
