package org.brailleblaster.libembosser.drivers.utils.document.events;

public final class RowGap extends BaseValueOption<Integer> implements DocumentOption, VolumeOption, SectionOption, PageOption, RowOption {
	public RowGap(Integer value) {
		super(value);
	}
}