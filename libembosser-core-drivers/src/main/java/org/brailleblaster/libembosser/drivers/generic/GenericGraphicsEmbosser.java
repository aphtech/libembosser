package org.brailleblaster.libembosser.drivers.generic;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Map;

import org.brailleblaster.libembosser.drivers.utils.BaseGraphicsEmbosser;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.PaperSize;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableMap;

public class GenericGraphicsEmbosser extends BaseGraphicsEmbosser {

	private final Font font;
	
	public GenericGraphicsEmbosser() {
		super("libembosser.generic.graphics", "Generic", "Graphics embosser");
		Font baseFont;
		try {
			baseFont = Font.createFont(Font.TRUETYPE_FONT, GenericGraphicsEmbosser.class.getResourceAsStream("/org/brailleblaster/libembosser/drivers/fonts/APH_Braille_Font-6.otf"));
		} catch (FontFormatException | IOException e) {
			throw new RuntimeException("Problem creating font, should not occur", e);
		}
		font = baseFont.deriveFont(26.0f);
	}

	@Override
	public Rectangle getMaximumPaper() {
		return PaperSize.B0.getSize();
	}

	@Override
	public Rectangle getMinimumPaper() {
		return PaperSize.A10.getSize();
	}

	@Override
	public boolean supportsInterpoint() {
		return false;
	}

	@Override
	public Map<TextAttribute, Object> getBrailleAttributes(BrlCell cell) {
		return ImmutableMap.of(TextAttribute.FONT, font);
	}

}
