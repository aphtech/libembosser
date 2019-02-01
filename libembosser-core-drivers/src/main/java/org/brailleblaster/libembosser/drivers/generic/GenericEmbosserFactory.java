package org.brailleblaster.libembosser.drivers.generic;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class GenericEmbosserFactory implements IEmbosserFactory {
	private static final Logger log = LoggerFactory.getLogger(GenericEmbosserFactory.class);
	public final static Rectangle LARGE_GENERIC_PAPER = new Rectangle(new BigDecimal("1000"), new BigDecimal("1000"));
	public static final Rectangle SMALL_GENERIC_PAPER = new Rectangle(new BigDecimal("30"), new BigDecimal("30"));
	private List<IEmbosser> embossers;
	public GenericEmbosserFactory() {
		ImmutableList.Builder<IEmbosser> builder = ImmutableList.<IEmbosser>builder()
				.add(new GenericTextEmbosser("libembosser.generic.text", "Text only", LARGE_GENERIC_PAPER, SMALL_GENERIC_PAPER))
				.add(new GenericTextEmbosser("libembosser.generic.text_with_margins", "Text with margins", LARGE_GENERIC_PAPER, SMALL_GENERIC_PAPER, true));
		try {
			builder.add(new GenericGraphicsEmbosser());
		} catch(Exception e) {
			// We just don't add the generic graphics driver, log the fact
			log.warn("Unable to create generic graphics embosser driver", e);
		}
		embossers = builder.build();
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
