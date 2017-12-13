package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.math.BigDecimal;
import java.util.List;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class ETFactory implements IEmbosserFactory {
	public static final Rectangle TWELVE_BY_FOURTEEN_PAPER = new Rectangle(new BigDecimal("305"), new BigDecimal("356"));
	private final List<IEmbosser> embossers;
	public ETFactory() {
		embossers = ImmutableList.of(new EnablingTechnologiesEmbosser("Phoenix Gold", TWELVE_BY_FOURTEEN_PAPER), new EnablingTechnologiesEmbosser("Cyclone", TWELVE_BY_FOURTEEN_PAPER));
	}

	@Override
	public List<IEmbosser> getEmbossers() {
		return embossers;
	}

}
