package org.brailleblaster.libembosser.embossing.attribute;

import java.util.Objects;

import org.brailleblaster.libembosser.spi.EmbossingAttribute;

public abstract class ObjectSyntax<T> implements EmbossingAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T value;
	public ObjectSyntax(T value) {
		this.value = value;
	}
	public T getValue() {
		return value;
	}
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ObjectSyntax)) {
			return false;
		}
		ObjectSyntax<?> other = (ObjectSyntax<?>) obj;
		return Objects.equals(value, other.value);
	}
}
