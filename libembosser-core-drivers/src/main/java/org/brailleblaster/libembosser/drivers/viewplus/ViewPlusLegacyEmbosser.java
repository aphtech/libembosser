package org.brailleblaster.libembosser.drivers.viewplus;

import java.io.InputStream;
import java.util.EnumSet;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;

/**
 * A ViewPlus embosser driver using legacy mode.
 * 
 * @author Michael Whapples
 *
 */
public class ViewPlusLegacyEmbosser extends BaseTextEmbosser {
	public ViewPlusLegacyEmbosser(String id, String model, Rectangle maxPaper,
			Rectangle minPaper) {
		super(id, "ViewPlus (Legacy)", model, maxPaper, minPaper);
	}
	private final static Version API_VERSION = new Version(1, 0);
	@Override
	public Version getApiVersion() {
		return API_VERSION;
	}
	@Override
	public EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF);
	}
	@Override
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format,
			EmbossProperties embossProperties) throws EmbossException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean supportsInterpoint() {
		// For now say they don't do interpoint.
		// In fact some do and even those which cannot do it automatically supposedly can by having the user manually flip the paper.
		return false;
	}
}
