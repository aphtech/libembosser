/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.braillo;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.brailleblaster.libembosser.spi.Embosser;
import org.testng.annotations.Test;

public class BrailloEmbosserFactoryTest {
	@Test
	public void testEmbosserList() {
		BrailloEmbosserFactory factory = new BrailloEmbosserFactory();
		List<Embosser> embossers = factory.getEmbossers();
		assertThat(embossers).filteredOn(e -> "libembosser.braillo.200".contentEquals(e.getId())).hasSize(1).element(0)
				.hasFieldOrPropertyWithValue("model", "Braillo 200")
				.matches(e -> BigDecimal.valueOf(330.0).compareTo(e.getMaximumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(140.0).compareTo(e.getMinimumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(356).compareTo(e.getMaximumPaper().getHeight()) == 0)
				.matches(e -> BigDecimal.valueOf(102).compareTo(e.getMinimumPaper().getHeight()) == 0);
		assertThat(embossers).filteredOn(e -> "libembosser.braillo.400s".contentEquals(e.getId())).hasSize(1).element(0)
				.hasFieldOrPropertyWithValue("model", "Braillo 400S")
				.matches(e -> BigDecimal.valueOf(330.0).compareTo(e.getMaximumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(140.0).compareTo(e.getMinimumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(356).compareTo(e.getMaximumPaper().getHeight()) == 0)
				.matches(e -> BigDecimal.valueOf(102).compareTo(e.getMinimumPaper().getHeight()) == 0);
		assertThat(embossers).filteredOn(e -> "libembosser.braillo.400sr".contentEquals(e.getId())).hasSize(1)
				.element(0).hasFieldOrPropertyWithValue("model", "Braillo 400SR")
				.matches(e -> BigDecimal.valueOf(330.0).compareTo(e.getMaximumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(140.0).compareTo(e.getMinimumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(356).compareTo(e.getMaximumPaper().getHeight()) == 0)
				.matches(e -> BigDecimal.valueOf(102).compareTo(e.getMinimumPaper().getHeight()) == 0);
		assertThat(embossers).filteredOn(e -> "libembosser.braillo.600".contentEquals(e.getId())).hasSize(1).element(0)
				.hasFieldOrPropertyWithValue("model", "Braillo 600")
				.matches(e -> BigDecimal.valueOf(330.0).compareTo(e.getMaximumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(140.0).compareTo(e.getMinimumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(356).compareTo(e.getMaximumPaper().getHeight()) == 0)
				.matches(e -> BigDecimal.valueOf(102).compareTo(e.getMinimumPaper().getHeight()) == 0);
		assertThat(embossers).filteredOn(e -> "libembosser.braillo.600sr".contentEquals(e.getId())).hasSize(1)
				.element(0).hasFieldOrPropertyWithValue("model", "Braillo 600SR")
				.matches(e -> BigDecimal.valueOf(330.0).compareTo(e.getMaximumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(140.0).compareTo(e.getMinimumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(356).compareTo(e.getMaximumPaper().getHeight()) == 0)
				.matches(e -> BigDecimal.valueOf(102).compareTo(e.getMinimumPaper().getHeight()) == 0);
		assertThat(embossers).filteredOn(e -> "libembosser.braillo.270v12".contentEquals(e.getId())).hasSize(1)
				.element(0).hasFieldOrPropertyWithValue("model", "Braillo 270")
				.matches(e -> BigDecimal.valueOf(330.0).compareTo(e.getMaximumPaper().getWidth()) == 0)
				.matches(e -> BigDecimal.valueOf(165.0).compareTo(e.getMinimumPaper().getWidth()) == 0)
		.matches(e -> BigDecimal.valueOf(356).compareTo(e.getMaximumPaper().getHeight()) == 0)
		.matches(e -> BigDecimal.valueOf(102).compareTo(e.getMinimumPaper().getHeight()) == 0);
	}
}
