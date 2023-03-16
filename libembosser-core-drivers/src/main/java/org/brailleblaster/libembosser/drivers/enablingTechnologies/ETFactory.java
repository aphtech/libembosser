/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import com.google.common.collect.ImmutableList;
import org.brailleblaster.libembosser.spi.Embosser;
import org.brailleblaster.libembosser.spi.EmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ETFactory implements EmbosserFactory {
	public static final Rectangle FIFTEEN_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("381"), new BigDecimal("356"));
	public static final Rectangle THIRTEEN_AND_QUARTER_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("337"), new BigDecimal("356"));
	public static final Rectangle TWELVE_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("305"), new BigDecimal("356"));
	public static final Rectangle EIGHT_AND_HALF_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("216"), new BigDecimal("356"));
	public static final Rectangle ONE_AND_HALF_BY_THREE_PAPER = new Rectangle(new BigDecimal("38"), new BigDecimal("76"));
	private final List<Embosser> embossers;
	public ETFactory() {
		embossers = ImmutableList.<Embosser>builder()
				.add(new EnablingTechnologiesEmbosser(Model.PHOENIX_GOLD, TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser(Model.PHOENIX_SILVER, TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser(Model.CYCLONE, TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser(Model.TRIDENT, TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser(Model.BOOK_MAKER, TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser(Model.BRAILLE_EXPRESS, TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser(Model.THOMAS, FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser(Model.THOMAS_PRO, FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser(Model.JULIET_CLASSIC, FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser(Model.JULIET_PRO, FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser(Model.JULIET_PRO60, FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser(Model.ET, FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser(Model.ROMEO_ATTACHE, EIGHT_AND_HALF_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser(Model.ROMEO_ATTACHE_PRO, EIGHT_AND_HALF_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser(Model.ROMEO_PRO50, THIRTEEN_AND_QUARTER_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser(Model.ROMEO25, THIRTEEN_AND_QUARTER_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.build();
	}

	@NotNull
    @Override
	public List<Embosser> getEmbossers() {
		return embossers;
	}

}
