/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.viewplus;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.stream.Stream;

import org.brailleblaster.libembosser.drivers.utils.BaseGraphicsEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DefaultNotificationImpl;
import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.LayoutHelper;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Notification;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Notification.NotificationType;
import org.jetbrains.annotations.NotNull;

public class ViewPlusEmbosser extends BaseGraphicsEmbosser {
	private final Rectangle minPaper;
	private final Rectangle maxPaper;
	private final boolean duplex;

	public ViewPlusEmbosser(String id, String model, Rectangle minPaper, Rectangle maxPaper, boolean duplex) {
		this(id, "ViewPlus Technologies", model, minPaper, maxPaper, duplex);
	}

	public ViewPlusEmbosser(String id, String manufacturer, String model, Rectangle minPaper, Rectangle maxPaper,
			boolean duplex) {
		super(id, manufacturer, model);
		this.minPaper = checkNotNull(minPaper);
		this.maxPaper = checkNotNull(maxPaper);
		this.duplex = duplex;
	}

	@NotNull
    @Override
	public Rectangle getMaximumPaper() {
		return maxPaper;
	}

	@NotNull
	@Override
	public Rectangle getMinimumPaper() {
		return minPaper;
	}

	@Override
	public boolean supportsInterpoint() {
		return duplex;
	}

	@NotNull
	@Override
	public LayoutHelper getLayoutHelper(BrlCell cell) {
		return new ViewPlusLayoutHelper();
	}

	@Override
	public Stream<Notification> checkPrerequisites() {
		return checkFontInstalled() ? Stream.of(new DefaultNotificationImpl(NotificationType.WARNING,
				"org.brailleblaster.libembosser.drivers.i18n.ViewPlus", "NoFont")) : Stream.empty();
	}

	@NotNull
	@Override
	public Stream<Notification> checkEmboss(int cellsPerLine, int linesPerPage, EmbossingAttributeSet attributes) {
		return checkFontInstalled() ? Stream.of(new DefaultNotificationImpl(NotificationType.WARNING,
				"org.brailleblaster.libembosser.drivers.i18n.ViewPlus", "NoFont")) : Stream.empty();
	}

	private boolean checkFontInstalled() {
		Map<TextAttribute, Object> attrs = getLayoutHelper(BrlCell.NLS).getBrailleAttributes(BrlCell.NLS);
		Font font = Font.getFont(attrs);
		return !font.getName().equals("Braille29");
	}
}
