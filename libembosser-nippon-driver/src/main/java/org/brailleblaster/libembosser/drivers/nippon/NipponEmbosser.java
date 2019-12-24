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

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getManufacturer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModel() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getMinimumPaper() {
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}

}
