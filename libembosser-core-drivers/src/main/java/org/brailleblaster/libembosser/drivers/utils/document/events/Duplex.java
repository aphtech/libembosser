package org.brailleblaster.libembosser.drivers.utils.document.events;

public final class Duplex extends BaseValueOption<Boolean> implements DocumentOption, VolumeOption, SectionOption {
	public Duplex(Boolean value) {
		super(value);
	}
}