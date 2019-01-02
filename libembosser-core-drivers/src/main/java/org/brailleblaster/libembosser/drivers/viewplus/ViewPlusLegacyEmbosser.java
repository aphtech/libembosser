package org.brailleblaster.libembosser.drivers.viewplus;

import java.io.InputStream;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.generic.GenericTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Rectangle;
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

	@Override
	public boolean supportsInterpoint() {
		// For now say they don't do interpoint.
		// In fact some do and even those which cannot do it automatically supposedly can by having the user manually flip the paper.
		return false;
	}
	@Override
	public void embossPef(PrintService printer, Document pef, EmbossingAttributeSet attributes) throws EmbossException {
		delegate.embossPef(printer, pef, attributes);
	}
	@Override
	public void embossPef(PrintService embosserDevice, InputStream pef, EmbossingAttributeSet attributes)
			throws EmbossException {
		delegate.embossPef(embosserDevice, pef, attributes);
	}
	@Override
	public void embossBrf(PrintService embosserDevice, InputStream brf, EmbossingAttributeSet attributes)
			throws EmbossException {
		delegate.embossBrf(embosserDevice, brf, attributes);
	}
}
