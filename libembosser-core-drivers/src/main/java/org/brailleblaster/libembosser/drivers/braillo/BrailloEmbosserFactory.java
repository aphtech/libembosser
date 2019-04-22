package org.brailleblaster.libembosser.drivers.braillo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.brailleblaster.libembosser.spi.Embosser;
import org.brailleblaster.libembosser.spi.EmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class BrailloEmbosserFactory implements EmbosserFactory {
	private final List<Embosser> embossers;
	public BrailloEmbosserFactory() {
		ImmutableList.Builder<Embosser> eb = new ImmutableList.Builder<Embosser>();
		eb.add(new Braillo200Embosser("libembosser.braillo.200", "Braillo 200", new Rectangle(BigDecimal.valueOf(330.0), BigDecimal.valueOf(14.0/25.4)), new Rectangle(BigDecimal.valueOf(140.0), BigDecimal.valueOf(4.0/25.4)), true));
		embossers = eb.build();
	}

	@Override
	public List<Embosser> getEmbossers() {
		return embossers;
	}

	@Override
	public List<Embosser> getEmbossers(Locale locale) {
		return getEmbossers();
	}

}
