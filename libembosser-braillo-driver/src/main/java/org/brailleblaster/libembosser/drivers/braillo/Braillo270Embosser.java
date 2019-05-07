package org.brailleblaster.libembosser.drivers.braillo;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Rectangle;

public class Braillo270Embosser extends BaseTextEmbosser {
	Braillo270Embosser(String id, String model, Rectangle maxPaper, Rectangle minPaper) {
		super(id, "Braillo", model, maxPaper, minPaper);
	}
	@Override
	public boolean supportsInterpoint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected DocumentToByteSourceHandler createHandler(EmbossingAttributeSet attributes) {
		// TODO Auto-generated method stub
		return null;
	}

}
