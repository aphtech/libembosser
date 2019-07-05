package org.brailleblaster.libembosser.drivers.utils.document.events;

public final class CellsPerLine extends BaseValueOption<Integer> implements DocumentOption, VolumeOption, SectionOption, PageOption {
	public CellsPerLine(Integer value) {
		super(value);
	}
}