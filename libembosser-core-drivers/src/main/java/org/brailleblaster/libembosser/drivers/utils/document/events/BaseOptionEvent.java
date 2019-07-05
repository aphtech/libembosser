package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public abstract class BaseOptionEvent<T extends Option> implements OptionEvent, DocumentEvent {
	private Set<T> options;
	protected BaseOptionEvent() {
		this.options = ImmutableSet.of();
	}
	protected BaseOptionEvent(Set<T> options) {
		this.options = ImmutableSet.copyOf(options);
	}
	@Override
	public Set<T> getOptions() {
		return options;
	}
}