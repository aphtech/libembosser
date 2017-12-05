package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.util.List;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;

import com.google.common.collect.ImmutableList;

public class ETFactory implements IEmbosserFactory {
	List<IEmbosser> embossers;
	public ETFactory() {
		embossers = ImmutableList.of();
	}

	@Override
	public List<IEmbosser> getEmbossers() {
		return embossers;
	}

}
