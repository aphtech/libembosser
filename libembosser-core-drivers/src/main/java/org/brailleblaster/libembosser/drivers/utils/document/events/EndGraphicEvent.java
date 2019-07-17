package org.brailleblaster.libembosser.drivers.utils.document.events;

public class EndGraphicEvent implements DocumentEvent {
	@Override
	public boolean equals(Object obj) {
		return obj != null && getClass().equals(obj.getClass());
	}
	@Override
	public int hashCode() {
		return 1;
	}
}