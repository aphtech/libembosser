package org.brailleblaster.libembosser.drivers.viewplus;

import java.util.List;
import java.util.Locale;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;

import com.google.common.collect.ImmutableList;

public class ViewPlusEmbosserFactory implements IEmbosserFactory {
	private ImmutableList<IEmbosser> embossers = ImmutableList.of(new ViewPlusEmbosser());
	// private ImmutableList<IEmbosser> embossers = ImmutableList.of();

	@Override
	public List<IEmbosser> getEmbossers() {
		return embossers;
	}

	@Override
	public List<IEmbosser> getEmbossers(Locale locale) {
		return embossers;
	}

}
