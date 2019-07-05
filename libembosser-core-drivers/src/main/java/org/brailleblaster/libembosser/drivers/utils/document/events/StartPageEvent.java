package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartPageEvent extends BaseOptionEvent<PageOption> {
	public StartPageEvent() {
		super();
	}
	public StartPageEvent(Set<PageOption> options) {
		super(options);
	}
}