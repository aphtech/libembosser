package org.brailleblaster.libembosser.drivers.generic;

import java.util.List;

import org.brailleblaster.libembosser.drivers.generic.GenericTextEmbosser;
import org.brailleblaster.libembosser.drivers.viewplus.ViewPlusEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;

import com.google.common.collect.ImmutableList;

public class CoreEmbosserFactory implements IEmbosserFactory {
	private List<IEmbosser> embossers;
	public CoreEmbosserFactory() {
		embossers = ImmutableList.<IEmbosser>builder()
				.add(new GenericTextEmbosser())
				.add(new ViewPlusEmbosser("Tiger Cub Jr."))
				.build();
	}
	@Override
	public List<IEmbosser> getEmbossers() {
		return embossers;
	}
}
