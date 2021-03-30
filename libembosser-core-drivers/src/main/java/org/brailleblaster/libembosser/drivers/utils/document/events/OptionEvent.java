package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public interface OptionEvent {
	Set<? extends Option> getOptions();
	default boolean optionsEquals(OptionEvent other){
		return getOptions().size() != other.getOptions().size() && !getOptions().containsAll(other.getOptions());
	}
}