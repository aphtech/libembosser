package org.brailleblaster.libembosser.drivers.nippon;

import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

import javax.print.PrintService;
import javax.print.StreamPrintServiceFactory;

import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.Embosser;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Notification;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.w3c.dom.Document;

public class NipponEmbosser implements Embosser {
	private final String id;
	private final String manufacturer;
	private final String model;
	private final Rectangle minimumPaper;
	private final Rectangle maximumPaper;
	public NipponEmbosser(String id, String model, Rectangle maximumPaper, Rectangle minimumPaper) {
		this.id = id;
		this.manufacturer = "Nippon";
		this.model = model;
		this.maximumPaper = maximumPaper;
		this.minimumPaper = minimumPaper;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getManufacturer() {
		return manufacturer;
	}

	@Override
	public String getModel() {
		return model;
	}

	@Override
	public void embossPef(PrintService embosserDevice, Document pef, EmbossingAttributeSet attributes)
			throws EmbossException {
		// TODO Auto-generated method stub

	}

	@Override
	public void embossBrf(PrintService embosserDevice, InputStream brf, EmbossingAttributeSet attributes)
			throws EmbossException {
		// TODO Auto-generated method stub

	}

	@Override
	public Rectangle getMaximumPaper() {
		return maximumPaper;
	}

	@Override
	public Rectangle getMinimumPaper() {
		return minimumPaper;
	}

	@Override
	public boolean supportsInterpoint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<StreamPrintServiceFactory> getStreamPrintServiceFactory() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Stream<Notification> checkPrerequisites() {
		// TODO Auto-generated method stub
		return Stream.empty();
	}

}
