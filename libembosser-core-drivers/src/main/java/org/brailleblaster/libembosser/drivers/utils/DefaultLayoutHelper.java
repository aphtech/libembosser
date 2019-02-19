package org.brailleblaster.libembosser.drivers.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Map;

import org.brailleblaster.libembosser.drivers.generic.GenericGraphicsEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.LayoutHelper;

import com.google.common.collect.ImmutableMap;

public class DefaultLayoutHelper implements LayoutHelper {
	private final Map<TextAttribute, Object> brailleAttributes;

	public DefaultLayoutHelper() {
		Font baseFont;
		try {
			baseFont = Font.createFont(Font.TRUETYPE_FONT, GenericGraphicsEmbosser.class.getResourceAsStream("/org/brailleblaster/libembosser/drivers/fonts/APH_Braille_Font-6.otf"));
		} catch (FontFormatException | IOException e) {
			throw new RuntimeException("Problem creating font, should not occur", e);
		}
		Font font = baseFont.deriveFont(26.0f);
		brailleAttributes = ImmutableMap.of(TextAttribute.FONT, font);
	}

	@Override
	public Map<TextAttribute, Object> getBrailleAttributes() {
		return brailleAttributes;
	}

	@Override
	public double calculateMargin(double desiredWidth) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double calculateBackMargin(double desiredWidth, double frontMargin) {
		// TODO Auto-generated method stub
		return 0;
	}

}