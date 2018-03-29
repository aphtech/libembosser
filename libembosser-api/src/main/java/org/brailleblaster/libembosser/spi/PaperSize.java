package org.brailleblaster.libembosser.spi;

import java.math.BigDecimal;

public enum PaperSize {
	BRAILLE_11_5X11("11.5x11", "292.1", "279.4"),
	A0("A0", "841", "1189"),
	A1("A1", "594", "841"),
	A2("A2", "420", "594"),
	A3("A3", "297", "420"),
	A4("A4", "210", "297"),
	A5("A5", "148", "210"),
	A6("A6", "105", "148"),
	A7("A7", "74", "105"),
	A8("A8", "52", "74"),
	A9("A9", "37", "52"),
	A10("A10", "26", "37"),
	B0("B0", "1000", "1414"),
	B1("B1", "707", "1000"),
	B2("B2", "500", "707"),
	B3("B3", "353", "500"),
	B4("B4", "250", "353"),
	B5("B5", "176", "250"),
	B6("B6", "125", "176"),
	B7("B7", "88", "125"),
	B8("B8", "62", "88"),
	B9("B9", "44", "62"),
	B10("B10", "31", "44"),
	HALF_LETTER("Half letter", "139.7", "216"),
	LETTER("Letter", "216", "279.4"),
	LEGAL("Legal", "216", "355.6"),
	JUNIOR_LEGAL("Junior legal", "127", "203.2"),
	LEDGER("Ledger", "279.4", "431.8");
	private String displayName;
	private Rectangle size;
	private PaperSize(String name, String width, String height) {
		this.displayName = name;
		size = new Rectangle(new BigDecimal(width), new BigDecimal(height));
	}
	public String getDisplayName() {
		return displayName;
	}
	public Rectangle getSize() {
		return size;
	}
}
