package org.brailleblaster.libembosser.drivers.utils.document.events;

public final class LinesPerPage extends BaseValueOption<Integer> implements DocumentOption, VolumeOption, SectionOption, PageOption {
	public LinesPerPage(Integer value) {
		super(value);
	}
}