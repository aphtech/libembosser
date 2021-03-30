package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartVolumeEvent extends BaseOptionEvent<VolumeOption> {
	public StartVolumeEvent() {
		super();
	}
	public StartVolumeEvent(Set<VolumeOption> options) {
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
		StartVolumeEvent other = (StartVolumeEvent)obj;
		return !optionsEquals(other);
	}
	@Override
	public int hashCode() {
		return 1;
	}
}