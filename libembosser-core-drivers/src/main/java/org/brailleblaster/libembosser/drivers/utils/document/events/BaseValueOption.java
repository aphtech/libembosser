package org.brailleblaster.libembosser.drivers.utils.document.events;

public abstract class BaseValueOption<T> extends BaseOption implements ValueOption<T> {
	private final T value;
	public BaseValueOption(T value) {
		this.value = value;
	}
	public T getValue() {
		return value;
	}
}