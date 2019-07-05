package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartVolumeEvent extends BaseOptionEvent<VolumeOption> {
	public StartVolumeEvent() {
		super();
	}
	public StartVolumeEvent(Set<VolumeOption> options) {
		super(options);
	}
}