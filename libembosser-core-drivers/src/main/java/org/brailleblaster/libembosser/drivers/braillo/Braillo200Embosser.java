package org.brailleblaster.libembosser.drivers.braillo;

import java.util.Optional;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.PageFilterByteSourceHandler;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Rectangle;

public class Braillo200Embosser extends BaseTextEmbosser {
	Braillo200Embosser(String id, String model, Rectangle maxPaper, Rectangle minPaper) {
		super(id, "Braillo", model, maxPaper, minPaper);
	}
	protected PageFilterByteSourceHandler createHandler(EmbossingAttributeSet attributes) {
		int copies = Optional.ofNullable((Copies)(attributes.get(Copies.class))).map(c -> c.getValue()).orElse(1);
		Braillo200DocumentHandler handler = new Braillo200DocumentHandler.Builder()
				.setCopies(copies)
				.build();
		PageRanges pages = Optional.ofNullable((PageRanges)(attributes.get(PageRanges.class))).orElseGet(() -> new PageRanges());
		return new PageFilterByteSourceHandler(handler, pages);
	}

	@Override
	public boolean supportsInterpoint() {
		// TODO Auto-generated method stub
		return false;
	}

}
