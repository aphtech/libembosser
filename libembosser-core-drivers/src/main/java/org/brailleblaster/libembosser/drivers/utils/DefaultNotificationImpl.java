/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import org.brailleblaster.libembosser.spi.Notification;

public class DefaultNotificationImpl implements Notification {
	private final NotificationType notificationType;
	private final String resourceId;
	private final String messageId;
	public DefaultNotificationImpl(NotificationType nt, String resourceId, String messageId) {
		this.notificationType = nt;
		this.resourceId = resourceId;
		this.messageId = messageId;
	}

	@Override
	public NotificationType getNotificationType() {
		return notificationType;
	}

	@Override
	public String getMessage() {
		return ResourceBundle.getBundle(resourceId).getString(messageId);
	}

	@Override
	public String getMessage(Locale locale) {
		return ResourceBundle.getBundle(resourceId, locale).getString(messageId);
	}

}
