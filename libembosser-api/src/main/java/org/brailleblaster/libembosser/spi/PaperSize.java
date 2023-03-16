/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

public enum PaperSize {
	BRAILLE_11_5X11("11.5x11", "292.1", "279.4"),
	// Needed for legacy
	// We should be able to remove after a few releases (may be in July 2018)
	@Deprecated
	BRAILLE_11X11_5(BRAILLE_11_5X11),
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
	private PaperSize replacementSize;
	PaperSize(String name, String width, String height) {
		this.displayName = name;
		size = new Rectangle(new BigDecimal(width), new BigDecimal(height));
		// Not replaced as not deprecated so use null
		replacementSize = null;
	}
	PaperSize(PaperSize replacement) {
		this.replacementSize = checkNotNull(replacement);
		displayName = replacementSize.getDisplayName();
		size = replacementSize.getSize();
	}
	public String getDisplayName() {
		return displayName;
	}
	public Rectangle getSize() {
		return size;
	}
	/**
	 * Check if the paper size is deprecated and replaced by another.
	 * 
	 * When a paper size has been replaced by another this method will return true. For paper sizes which are deprecated and not replaced then they should simply be removed from the enum and a custom paper size should be used instead.
	 * 
	 * @return Whether this paper size is deprecated and replaced by another.
	 */
	public boolean isReplaced() {
		return replacementSize != null;
	}
	/** Get the most current implementation of tis paper size standard.
	 * 
	 * @return The most current paper size standard relating to this size.
	 */
	public PaperSize getCurrentStandard() {
		return replacementSize == null? this : replacementSize.getCurrentStandard();
	}
	/**
	 * Get all the paper sizes which have not been replaced by another.
	 * 
	 * @return An immutable list of the current paper sizes.
	 */
	public static List<PaperSize> getCurrentSizes() {
		return Arrays.stream(PaperSize.values()).filter(s -> !s.isReplaced()).collect(ImmutableList.toImmutableList());
	}
}
