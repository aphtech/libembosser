package org.brailleblaster.libembosser;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.brailleblaster.libembosser.spi.Embosser;
import org.brailleblaster.libembosser.spi.EmbosserFactory;

import com.google.common.collect.Streams;

public class EmbosserService {
	private static EmbosserService service;
	private ServiceLoader<EmbosserFactory> serviceLoader;
	private EmbosserService() {
		serviceLoader = ServiceLoader.load(EmbosserFactory.class);
	}
	public static synchronized EmbosserService getInstance() {
		if (service == null) {
			service = new EmbosserService();
		}
		return service;
	}
	public Stream<Embosser> getEmbosserStream() {
		return Streams.stream(serviceLoader).flatMap(ef -> ef.getEmbossers().stream());
	}
	public List<Embosser> getEmbossers() {
		return getEmbosserStream().collect(Collectors.toList());
	}
	public Embosser getEmbosser(String manufacturer, String model) {
		return getEmbosserStream().filter(e -> e.getManufacturer().equals(manufacturer) && e.getModel().equals(model)).findAny().orElseThrow(() -> new NoSuchElementException());
	}
	/**
	 * Find an embosser matching the ID.
	 * 
	 * @param id The id of the embosser to find.
	 * @return An embosser matching the id.
	 */
	public Embosser getEmbosser(String id) {
		return getEmbosserStream().filter(e -> e.getId().equals(id)).findAny().orElseThrow(() -> new NoSuchElementException(String.format("Cannot find embosser driver with \"%s\"", id)));
	}
}
