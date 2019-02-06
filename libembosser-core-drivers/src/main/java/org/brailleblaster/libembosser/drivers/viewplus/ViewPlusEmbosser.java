package org.brailleblaster.libembosser.drivers.viewplus;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Font;

import org.brailleblaster.libembosser.drivers.utils.BaseGraphicsEmbosser;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.Rectangle;

public class ViewPlusEmbosser extends BaseGraphicsEmbosser {
	private final Rectangle minPaper;
	private final Rectangle maxPaper;
	private final boolean duplex;
	public ViewPlusEmbosser(String id, String model, Rectangle minPaper, Rectangle maxPaper, boolean duplex) {
		super(id, "ViewPlus Technologies", model);
		this.minPaper = checkNotNull(minPaper);
		this.maxPaper = checkNotNull(maxPaper);
		this.duplex = duplex;
	}
	
	@Override
	public Rectangle getMaximumPaper() {
		return maxPaper;
	}

	@Override
	public Rectangle getMinimumPaper() {
		return minPaper;
	}

	@Override
	public boolean supportsInterpoint() {
		return duplex;
	}

	@Override
	public Font getFont(BrlCell cell) {
		return new Font("Braille29", Font.PLAIN, 29);
	}
	
}
