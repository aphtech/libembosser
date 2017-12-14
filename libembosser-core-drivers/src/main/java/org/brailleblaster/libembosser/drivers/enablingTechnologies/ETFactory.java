package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.math.BigDecimal;
import java.util.List;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class ETFactory implements IEmbosserFactory {
	public static final Rectangle TWELVE_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("305"), new BigDecimal("356"));
	public static final Rectangle ONE_AND_HALF_BY_THREE_PAPER = new Rectangle(new BigDecimal("38"), new BigDecimal("76"));
	private final List<IEmbosser> embossers;
	public ETFactory() {
		embossers = ImmutableList.<IEmbosser>builder()
				.add(new EnablingTechnologiesEmbosser("Phoenix Gold", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("Phoenix  silver", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("Cyclone", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, false))
				.add(new EnablingTechnologiesEmbosser("Trident", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("BookMaker", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.add(new EnablingTechnologiesEmbosser("Braille Express", TWELVE_BY_FOURTEEN_PAPER, ONE_AND_HALF_BY_THREE_PAPER, true))
				.build();
	}

	@Override
	public List<IEmbosser> getEmbossers() {
		return embossers;
	}

}
