package org.brailleblaster.libembosser.drivers.viewplus;

import java.io.InputStream;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.generic.GenericTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;
import org.w3c.dom.Document;

/**
 * A ViewPlus embosser driver using legacy mode.
 * 
 * @author Michael Whapples
 *
 */
public class ViewPlusLegacyEmbosser extends BaseTextEmbosser {
	// Just delegate to the generic text embosser driver until we get to supporting the ViewPlus legacy commands.
	private GenericTextEmbosser delegate;
	public ViewPlusLegacyEmbosser(String id, String model, Rectangle maxPaper,
			Rectangle minPaper) {
		super(id, "ViewPlus (Legacy)", model, maxPaper, minPaper);
		delegate = new GenericTextEmbosser("ViewPlus", model, maxPaper, minPaper);
	}
	private final static Version API_VERSION = new Version(1, 0);
	@Override
	public Version getApiVersion() {
		return API_VERSION;
	}
	@Override
	public boolean supportsInterpoint() {
		// For now say they don't do interpoint.
		// In fact some do and even those which cannot do it automatically supposedly can by having the user manually flip the paper.
		return false;
	}
	@Override
	public boolean embossPef(PrintService printer, Document pef, EmbossProperties props) throws EmbossException {
		return delegate.embossPef(printer, pef, props);
	}
	@Override
	public boolean embossPef(PrintService embosserDevice, InputStream pef, EmbossProperties embossProperties)
			throws EmbossException {
		return delegate.embossPef(embosserDevice, pef, embossProperties);
	}
	@Override
	public boolean embossBrf(PrintService embosserDevice, InputStream brf, EmbossProperties embossProperties)
			throws EmbossException {
		return delegate.embossBrf(embosserDevice, brf, embossProperties);
	}
}
