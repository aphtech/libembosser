package org.brailleblaster.libembosser.drivers.indexBraille;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class IndexBrailleFactory implements IEmbosserFactory {
	private static final Rectangle FOUR_BY_THREE_PAPER = new Rectangle(new BigDecimal("101.6"), new BigDecimal("76.2"));
	private static final Rectangle TWELVE_AND_HALF_BY_TWENTY_FOUR_PAPER = new Rectangle(new BigDecimal("317.5"), new BigDecimal("609.6"));
	private List<IEmbosser> embossers;
	public IndexBrailleFactory() {
		embossers = ImmutableList.<IEmbosser>builder()
				// The Enabling Technologies Romeo60 and Juliet120 are based on Index Basic D V5
				.add(new IndexBrailleEmbosser("libembosser.ib.Romeo60", "Enabling Technologies", "Romeo 60", TWELVE_AND_HALF_BY_TWENTY_FOUR_PAPER, FOUR_BY_THREE_PAPER))
				.add(new IndexBrailleEmbosser("libembosser.ib.Juliet120", "Enabling Technologies", "Juliet 120", TWELVE_AND_HALF_BY_TWENTY_FOUR_PAPER, FOUR_BY_THREE_PAPER))
				.add(new IndexBrailleEmbosser("libembosser.ib.BasicDV5", "Index Braille", "Basic-D V5", new Rectangle(new BigDecimal("325"), new BigDecimal("431")), new Rectangle(new BigDecimal("100"), new BigDecimal("25"))))
				.add(new IndexBrailleEmbosser("libembosser.ib.EverestDV5", "Index Braille", "Everest-D V5", new Rectangle(new BigDecimal("297"), new BigDecimal("590")), new Rectangle(new BigDecimal("130"), new BigDecimal("100"))))
				.build();
	}
	@Override
	public List<IEmbosser> getEmbossers() {
		return embossers;
	}
	@Override
	public List<IEmbosser> getEmbossers(Locale locale) {
		return embossers;
	}
}
