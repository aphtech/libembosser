/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ModelTest {
	@DataProvider(name="modelProvider")
	public Object[][] modelProvider() {
		return new Object[][] {
			{Model.BOOK_MAKER, "libembosser.et.bookmaker", "BookMaker", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.BRAILLE_EXPRESS, "libembosser.et.braille_express", "Braille Express", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.THOMAS, "libembosser.et.thomas", "Thomas", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.THOMAS_PRO, "libembosser.et.thomas_pro", "Thomas Pro", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.JULIET_CLASSIC, "libembosser.et.juliet_classic", "Juliet Classic",  new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.JULIET_PRO, "libembosser.et.juliet_pro", "Juliet Pro", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.JULIET_PRO60, "libembosser.et.juliet_pro60", "Juliet Pro60", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.ET, "libembosser.et.et", "ET", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.ROMEO_PRO50, "libembosser.et.romeo_pro50", "Romeo Pro50", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.ROMEO25, "libembosser.et.romeo_25", "Romeo25", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {}},
			{Model.PHOENIX_GOLD, "libembosser.et.phoenix_gold", "Phoenix Gold", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {0x1a}},
			{Model.PHOENIX_SILVER, "libembosser.et.phoenix_silver", "Phoenix  silver", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {0x1a}},
			{Model.CYCLONE, "libembosser.et.cyclone", "Cyclone", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {0x1a}},
			{Model.TRIDENT, "libembosser.et.trident", "Trident", new byte[] {'\r', '\n'}, new byte[] {'\r', '\n', '\f'}, new byte[] {0x1a}},
			{Model.ROMEO_ATTACHE, "libembosser.et.romeo_attache", "Romeo Attach\u00e9", new byte[] {'\n'}, new byte[] {'\n', '\f'}, new byte[] {}},
			{Model.ROMEO_ATTACHE_PRO, "libembosser.et.romeo_attache_pro", "Romeo Attach\u00e9 Pro",new byte[] {'\n'}, new byte[] {'\n', '\f'}, new byte[] {}},
		};
	}
	@Test(dataProvider="modelProvider")
	public void testModelData(Model model, String id, String name, byte[] lineEnd, byte[] pageEnd, byte[] docEnd) {
		assertThat(model)
		.hasFieldOrPropertyWithValue("lineEnd", lineEnd)
		.hasFieldOrPropertyWithValue("pageEnd", pageEnd)
		.hasFieldOrPropertyWithValue("docEnd", docEnd)
		.hasFieldOrPropertyWithValue("id", id)
		.hasFieldOrPropertyWithValue("name", name);
	}
}
