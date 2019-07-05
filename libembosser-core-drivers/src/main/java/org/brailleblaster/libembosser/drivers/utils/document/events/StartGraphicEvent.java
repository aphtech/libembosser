package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartGraphicEvent extends BaseOptionEvent<GraphicOption> {
	public StartGraphicEvent() {
		super();
	}
	public StartGraphicEvent(Set<GraphicOption> options) {
		super(options);
	}
}