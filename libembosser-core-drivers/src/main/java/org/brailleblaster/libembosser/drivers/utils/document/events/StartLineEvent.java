package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartLineEvent extends BaseOptionEvent<RowOption> {
	public StartLineEvent() {
		super();
	}
	public StartLineEvent(Set<RowOption> options) {
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
		StartLineEvent other = (StartLineEvent)obj;
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