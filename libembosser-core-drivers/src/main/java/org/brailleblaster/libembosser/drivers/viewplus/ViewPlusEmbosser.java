package org.brailleblaster.libembosser.drivers.viewplus;

import java.awt.Font;

import org.brailleblaster.libembosser.drivers.utils.BaseGraphicsEmbosser;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.PaperSize;
import org.brailleblaster.libembosser.spi.Rectangle;

public class ViewPlusEmbosser extends BaseGraphicsEmbosser {

	@Override
	public String getId() {
		return "libembosser.vp.test_embosser";
	}

	@Override
	public String getManufacturer() {
		return "ViewPlus Technologies";
	}

	@Override
	public String getModel() {
		return "Test model";
	}

	

	@Override
	public Rectangle getMaximumPaper() {
		return PaperSize.A3.getSize();
	}

	@Override
	public Rectangle getMinimumPaper() {
		return PaperSize.B10.getSize();
	}

	@Override
	public boolean supportsInterpoint() {
		// For now don't support interpoint.
		return false;
	}

	@Override
	public Font getFont(BrlCell cell) {
		return new Font("Braille29", Font.PLAIN, 29);
	}
	
}
