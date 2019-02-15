package org.brailleblaster.libembosser.drivers.viewplus;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.Map;

import org.brailleblaster.libembosser.drivers.utils.BaseGraphicsEmbosser;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableMap;

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
	public Map<TextAttribute, Object> getBrailleAttributes(BrlCell cell) {
		// Font font = new Font("Braille29", Font.PLAIN, 29);
		return ImmutableMap.of(TextAttribute.FAMILY, "Braille29", TextAttribute.SIZE, 29, TextAttribute.FOREGROUND, new Color(6, 7, 8));
	}
	
}
