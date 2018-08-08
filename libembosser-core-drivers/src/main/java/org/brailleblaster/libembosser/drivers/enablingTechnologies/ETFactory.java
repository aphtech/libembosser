package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class ETFactory implements IEmbosserFactory {
	public static final Rectangle FIFTEEN_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("381"), new BigDecimal("356"));
	public static final Rectangle THIRTEEN_AND_QUARTER_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("337"), new BigDecimal("356"));
	public static final Rectangle TWELVE_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("305"), new BigDecimal("356"));
	public static final Rectangle EIGHT_AND_HALF_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("216"), new BigDecimal("356"));
	public static final Rectangle ONE_AND_HALF_BY_THREE_PAPER = new Rectangle(new BigDecimal("38"), new BigDecimal("76"));
	private final List<IEmbosser> embossers;
	public ETFactory() {
		embossers = ImmutableList.<IEmbosser>builder()
				.add(new EnablingTechnologiesEmbosser("libembosser.et.phoenix_gold", "Phoenix Gold", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.phoenix_silver", "Phoenix  silver", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.cyclone", "Cyclone", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.trident", "Trident", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.bookmaker", "BookMaker", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.braille_express", "Braille Express", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.thomas", "Thomas", FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.thomas_pro", "Thomas Pro", FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.juliet_classic", "Juliet Classic", FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.juliet_pro", "Juliet Pro", FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.juliet_pro60", "Juliet Pro60", FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.et", "ET", FIFTEEN_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.romeo_attache", "Romeo Attach\u00e9", EIGHT_AND_HALF_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.romeo_attache_pro", "Romeo Attach\u00e9 Pro", EIGHT_AND_HALF_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.romeo_pro50", "Romeo Pro50", THIRTEEN_AND_QUARTER_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("libembosser.et.romeo_25", "Romeo25", THIRTEEN_AND_QUARTER_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.build();
	}

	@Override
	public List<IEmbosser> getEmbossers() {
		return embossers;
	}
	@Override
	public List<IEmbosser> getEmbossers(Locale locale) {
		// For now ignore the locale.
		return getEmbossers();
	}

}
