package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartLineEvent extends BaseOptionEvent<RowOption> {
	public StartLineEvent() {
		super();
	}
	public StartLineEvent(Set<RowOption> options) {
		super(options);
	}
}