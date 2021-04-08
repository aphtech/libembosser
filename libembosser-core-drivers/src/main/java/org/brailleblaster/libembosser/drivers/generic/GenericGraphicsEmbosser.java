package org.brailleblaster.libembosser.drivers.generic;

import org.brailleblaster.libembosser.drivers.utils.BaseGraphicsEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DefaultLayoutHelper;
import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.LayoutHelper;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.PaperSize;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.jetbrains.annotations.NotNull;

public class GenericGraphicsEmbosser extends BaseGraphicsEmbosser {
	
	public GenericGraphicsEmbosser() {
		super("libembosser.generic.graphics", "Generic", "Graphics embosser");
	}

	@NotNull
    @Override
	public Rectangle getMaximumPaper() {
		return PaperSize.B0.getSize();
	}

	@NotNull
	@Override
	public Rectangle getMinimumPaper() {
		return PaperSize.A10.getSize();
	}

	@Override
	public boolean supportsInterpoint() {
		return true;
	}

	@NotNull
	@Override
	public LayoutHelper getLayoutHelper(BrlCell cell) {
		return new DefaultLayoutHelper();
	}
}
