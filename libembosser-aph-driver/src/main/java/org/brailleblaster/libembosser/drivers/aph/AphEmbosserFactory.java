package org.brailleblaster.libembosser.drivers.aph;

import java.math.BigDecimal;
import java.util.List;

import org.brailleblaster.libembosser.drivers.indexBraille.IndexBrailleEmbosser;
import org.brailleblaster.libembosser.drivers.indexBraille.IndexBrailleFactory;
import org.brailleblaster.libembosser.drivers.viewplus.ViewPlusEmbosser;
import org.brailleblaster.libembosser.spi.Embosser;
import org.brailleblaster.libembosser.spi.EmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class AphEmbosserFactory implements EmbosserFactory {
	private List<Embosser> embossers;
	public AphEmbosserFactory() {
		embossers = ImmutableList.<Embosser>builder()
				.add(new ViewPlusEmbosser("libembosser.aph.PixBlaster", "APH", "PixBlaster", new Rectangle("176", "20"), new Rectangle("325", "610"), true))
				.add(new IndexBrailleEmbosser("libembosser.aph.PageBlaster", "APH", "PageBlaster", new Rectangle(new BigDecimal("325"), new BigDecimal("431.8")), new Rectangle(new BigDecimal("100"), new BigDecimal("25")), 49, IndexBrailleFactory.BASIC_D_SIDES))
				.build();
	}
	@Override
	public List<Embosser> getEmbossers() {
		return embossers;
	}
}
