package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartPageEvent extends BaseOptionEvent<PageOption> {
	public StartPageEvent() {
		super();
	}
	public StartPageEvent(Set<PageOption> options) {
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
		StartPageEvent other = (StartPageEvent)obj;
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