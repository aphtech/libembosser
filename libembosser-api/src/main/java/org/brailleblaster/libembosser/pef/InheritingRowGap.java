package org.brailleblaster.libembosser.pef;

public interface InheritingRowGap<T extends RowGap> extends RowGap, SubElement<T> {
	public default int getRowGapValue() {
		if (getRowGap() != null) {
			return getRowGap().intValue();
		} else if (getParent() != null) {
			return getParent().getRowGapValue();
		} else {
			return 0;
		}
	}
}
