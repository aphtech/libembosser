package org.brailleblaster.libembosser.drivers.viewplus;

import java.util.List;
import java.util.Locale;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;

public class ViewPlusEmbosserFactory implements IEmbosserFactory {
	private ImmutableList<IEmbosser> embossers;
	public ViewPlusEmbosserFactory() {
		embossers = new ImmutableList.Builder<IEmbosser>()
				.add(new ViewPlusEmbosser("libembosser.vp.embraille", "EmBraille", new Rectangle("77", "20"), new Rectangle("216", "610"), false))
				.add(new ViewPlusEmbosser("libembosser.vp.columbia", "Columbia", new Rectangle("176", "20"), new Rectangle("325", "610"), true))
				.add(new ViewPlusEmbosser("libembosser.vp.delta", "Delta", new Rectangle("176", "20"), new Rectangle("305", "432"), true))
				.add(new ViewPlusEmbosser("libembosser.vp.premier", "Premier", new Rectangle("101", "20"), new Rectangle("305", "610"), true))
				.add(new ViewPlusEmbosser("libembosser.vp.elite", "Elite", new Rectangle("101", "20"), new Rectangle("305", "610"), true))
				.add(new ViewPlusEmbosser("libembosser.vp.emprint", "Emprint", new Rectangle("110", "20"), new Rectangle("216", "432"), false))
				.add(new ViewPlusEmbosser("libembosser.vp.SpotDot", "SpotDot", new Rectangle("110", "20"), new Rectangle("216", "432"), false))
				.add(new ViewPlusEmbosser("libembosser.vp.max", "Max", new Rectangle("101", "20"), new Rectangle("356", "610"), false))
				.add(new ViewPlusEmbosser("libembosser.vp.cub", "Cub", new Rectangle("77", "20"), new Rectangle("216", "610"), false))
				.add(new ViewPlusEmbosser("libembosser.vp.cub_jr", "Cub JR.", new Rectangle("77", "20"), new Rectangle("216", "610"), false))
				.add(new ViewPlusEmbosser("libembosser.vp.braille_buddy", "IRIE", "Braille Buddy", new Rectangle("77", "20"), new Rectangle("216", "610"), false))
				.add(new ViewPlusEmbosser("libembosser.vp.braille_sheet_120", "IRIE", "Braille Sheet 120", new Rectangle("176", "20"), new Rectangle("305", "432"), true))
				.add(new ViewPlusEmbosser("libembosser.vp.braille_trac_120", "IRIE", "Braille Trac 120", new Rectangle("176", "20"), new Rectangle("325", "610"), true))
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
