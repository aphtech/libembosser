package org.brailleblaster.libembosser.drivers.indexBraille;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.spi.MultiSides;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class IndexBrailleFactory implements IEmbosserFactory {
	private static final EnumSet<MultiSides> BASIC_D_SIDES = EnumSet.of(MultiSides.INTERPOINT, MultiSides.P1ONLY, MultiSides.Z_FOLDING_DOUBLE_HORIZONTAL, MultiSides.Z_FOLDING_SINGLE_HORIZONTAL, MultiSides.Z_FOLDING_DOUBLE_VERTICAL, MultiSides.Z_FOLDING_SINGLE_VERTICAL);
	private static final EnumSet<MultiSides> EVEREST_SIDES = EnumSet.of(MultiSides.INTERPOINT, MultiSides.P1ONLY, MultiSides.SADDLE_STITCH_DOUBLE_SIDED, MultiSides.SADDLE_STITCH_SINGLE_SIDED);
	private static final EnumSet<MultiSides> BRAILLEBOX_SIDES = EVEREST_SIDES;
	private static final EnumSet<MultiSides> FANFOLD_SIDES = BASIC_D_SIDES;
	private static final EnumSet<MultiSides> ENABLING_SIDES = EnumSet.of(MultiSides.INTERPOINT, MultiSides.P1ONLY, MultiSides.Z_FOLDING_DOUBLE_VERTICAL, MultiSides.Z_FOLDING_SINGLE_VERTICAL, MultiSides.Z_FOLDING_DOUBLE_HORIZONTAL, MultiSides.Z_FOLDING_SINGLE_HORIZONTAL);
	private static final Rectangle FOUR_BY_THREE_PAPER = new Rectangle(new BigDecimal("101.6"), new BigDecimal("76.2"));
	private static final Rectangle TWELVE_AND_HALF_BY_TWENTY_FOUR_PAPER = new Rectangle(new BigDecimal("317.5"), new BigDecimal("609.6"));
	private static final Rectangle BRAILLEBOX_MAX_PAPER = new Rectangle(new BigDecimal("300"), new BigDecimal("440"));
	private static final Rectangle BRAILLEBOX_MIN_PAPER = new Rectangle(new BigDecimal("200"), new BigDecimal("250"));
	private List<IEmbosser> embossers;
	public IndexBrailleFactory() {
		embossers = ImmutableList.<IEmbosser>builder()
				// The Enabling Technologies Romeo60 and Juliet120 are based on Index Basic D V5
				.add(new IndexBrailleEmbosser("libembosser.ib.Romeo60", "Enabling Technologies", "Romeo 60", TWELVE_AND_HALF_BY_TWENTY_FOUR_PAPER, FOUR_BY_THREE_PAPER, 49, ENABLING_SIDES))
				.add(new IndexBrailleEmbosser("libembosser.ib.Juliet120", "Enabling Technologies", "Juliet 120", TWELVE_AND_HALF_BY_TWENTY_FOUR_PAPER, FOUR_BY_THREE_PAPER, 49, ENABLING_SIDES))
				.add(new IndexBrailleEmbosser("libembosser.ib.BasicDV5", "Index Braille", "Basic-D V5", new Rectangle(new BigDecimal("325"), new BigDecimal("431.8")), new Rectangle(new BigDecimal("100"), new BigDecimal("25")), 49, BASIC_D_SIDES))
				.add(new IndexBrailleEmbosser("libembosser.ib.BasicDV4", "Index Braille", "Basic-D V4", new Rectangle(new BigDecimal("330"), new BigDecimal("431.8")), new Rectangle(new BigDecimal("100"), new BigDecimal("25")), 49, BASIC_D_SIDES))
				.add(new IndexBrailleEmbosser("libembosser.ib.EverestDV5", "Index Braille", "Everest-D V5", new Rectangle(new BigDecimal("297.6"), new BigDecimal("590")), new Rectangle(new BigDecimal("130"), new BigDecimal("100")), 48, EVEREST_SIDES))
				.add(new IndexBrailleEmbosser("libembosser.ib.EverestDV4", "Index Braille", "Everest-D V4", new Rectangle(new BigDecimal("297.6"), new BigDecimal("590")), new Rectangle(new BigDecimal("130"), new BigDecimal("120")), 48, EVEREST_SIDES))
				.add(new IndexBrailleEmbosser("libembosser.ib.BrailleBoxV5", "Index Braille", "BrailleBox V5", BRAILLEBOX_MAX_PAPER, BRAILLEBOX_MIN_PAPER, 48, BRAILLEBOX_SIDES))
				.add(new IndexBrailleEmbosser("libembosser.ib.BrailleBoxV4", "Index Braille", "BrailleBox V4", BRAILLEBOX_MAX_PAPER, BRAILLEBOX_MIN_PAPER, 48, BRAILLEBOX_SIDES))
				.add(new IndexBrailleEmbosser("libembosser.ib.FanFoldV5", "Index Braille", "FanFold V5", new Rectangle(new BigDecimal("317.5"), new BigDecimal("304.8")), new Rectangle(new BigDecimal("127"), new BigDecimal("203.2")), 48, FANFOLD_SIDES))
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
