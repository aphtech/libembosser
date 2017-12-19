package org.brailleblaster.libembosser.drivers.generic;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.brailleblaster.libembosser.drivers.generic.GenericTextEmbosser;
import org.brailleblaster.libembosser.drivers.viewplus.ViewPlusLegacyEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class GenericEmbosserFactory implements IEmbosserFactory {
	public final static Rectangle LARGE_GENERIC_PAPER = new Rectangle(new BigDecimal("1000"), new BigDecimal("1000"));
	public static final Rectangle SMALL_GENERIC_PAPER = new Rectangle(new BigDecimal("30"), new BigDecimal("30"));
	private List<IEmbosser> embossers;
	public GenericEmbosserFactory() {
		embossers = ImmutableList.<IEmbosser>builder()
				.add(new GenericTextEmbosser("libembosser.generic.text", "Text only", LARGE_GENERIC_PAPER, SMALL_GENERIC_PAPER))
				.add(new GenericTextEmbosser("libembosser.generic.text_with_margins", "Text with margins", LARGE_GENERIC_PAPER, SMALL_GENERIC_PAPER, true))
				.build();
	}
	@Override
	public List<IEmbosser> getEmbossers() {
		return embossers;
	}
	@Override
	public List<IEmbosser> getEmbossers(Locale locale) {
		return getEmbossers();
	}
}
