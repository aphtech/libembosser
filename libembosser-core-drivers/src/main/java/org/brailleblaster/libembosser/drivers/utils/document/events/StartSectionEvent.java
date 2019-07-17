package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartSectionEvent extends BaseOptionEvent<SectionOption> {
	public StartSectionEvent() {
		super();
	}
	public StartSectionEvent(Set<SectionOption> options) {
		super(options);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StartSectionEvent other = (StartSectionEvent)obj;
		if (optionsEquals(other)) {
			return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		return 1;
	}
}