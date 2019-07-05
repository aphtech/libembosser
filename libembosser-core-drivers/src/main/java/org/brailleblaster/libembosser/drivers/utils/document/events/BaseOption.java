package org.brailleblaster.libembosser.drivers.utils.document.events;

public abstract class BaseOption {
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		return getClass().equals(other.getClass());
	}
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}