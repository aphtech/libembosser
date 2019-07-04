package org.brailleblaster.libembosser.drivers.utils;

import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;

@FunctionalInterface	
public interface DocumentHandler {
	public void onEvent(DocumentEvent event);
}