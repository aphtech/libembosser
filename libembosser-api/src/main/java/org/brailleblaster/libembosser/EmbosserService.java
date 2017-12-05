package org.brailleblaster.libembosser;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;

import com.google.common.collect.Streams;

public class EmbosserService {
	private static EmbosserService service;
	private ServiceLoader<IEmbosserFactory> serviceLoader;
	private EmbosserService() {
		serviceLoader = ServiceLoader.load(IEmbosserFactory.class);
	}
	public static synchronized EmbosserService getInstance() {
		if (service == null) {
			service = new EmbosserService();
		}
		return service;
	}
	public Stream<IEmbosser> getEmbosserStream() {
		return Streams.stream(serviceLoader).flatMap(ef -> ef.getEmbossers().stream());
	}
	public List<IEmbosser> getEmbossers() {
		return getEmbosserStream().collect(Collectors.toList());
	}
	public IEmbosser getEmbosser(String manufacturer, String model) {
		return getEmbosserStream().filter(e -> e.getManufacturer().equals(manufacturer) && e.getModel().equals(model)).findAny().orElseThrow(() -> new NoSuchElementException());
	}
}
