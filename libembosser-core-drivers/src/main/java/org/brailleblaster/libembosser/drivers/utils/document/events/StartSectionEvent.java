package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartSectionEvent extends BaseOptionEvent<SectionOption> {
	public StartSectionEvent() {
		super();
	}
	public StartSectionEvent(Set<SectionOption> options) {
		super(options);
	}
}