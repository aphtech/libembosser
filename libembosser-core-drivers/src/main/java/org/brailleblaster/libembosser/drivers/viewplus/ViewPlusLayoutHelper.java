package org.brailleblaster.libembosser.drivers.viewplus;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.Map;

import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.LayoutHelper;

import com.google.common.collect.ImmutableMap;

public class ViewPlusLayoutHelper implements LayoutHelper {

	private final Map<TextAttribute, Object> brailleAttributes;

	public ViewPlusLayoutHelper() {
		brailleAttributes = ImmutableMap.of(TextAttribute.FAMILY, "Braille29", TextAttribute.SIZE, 29, TextAttribute.FOREGROUND, new Color(6, 7, 8));
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
