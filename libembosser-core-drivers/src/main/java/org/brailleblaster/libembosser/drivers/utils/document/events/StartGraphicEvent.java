package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartGraphicEvent extends BaseOptionEvent<GraphicOption> {
	public StartGraphicEvent() {
		super();
	}
	public StartGraphicEvent(Set<GraphicOption> options) {
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
		StartGraphicEvent other = (StartGraphicEvent)obj;
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