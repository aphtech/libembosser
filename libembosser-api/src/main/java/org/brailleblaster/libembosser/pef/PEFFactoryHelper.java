package org.brailleblaster.libembosser.pef;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

class PEFFactoryHelper {
	private static PEFFactoryHelper instance = null;
	private ServiceLoader<PEFFactory> loader;
	private PEFFactoryHelper() {
		this.loader = ServiceLoader.load(PEFFactory.class);
	}
	static PEFFactoryHelper getinstance() {
		if (instance == null) {
			instance = new PEFFactoryHelper();
		}
		return instance;
	}
	PEFFactory loadPEFFactory() {
		return Streams.stream(loader).findAny().orElseThrow(() -> new NoSuchElementException("There are no PEF implementations installed.")); 
	}
}
