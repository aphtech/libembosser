package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartDocumentEvent extends BaseOptionEvent<DocumentOption> {
	public StartDocumentEvent() {
		super();
	}
	public StartDocumentEvent(Set<DocumentOption> options) {
		super(options);
	}
}