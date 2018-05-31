package org.brailleblaster.libembosser.pef;

public interface Section extends InheritingRowGap<Volume>, Rows, Cols, Duplex {
	public Page appendNewPage();
	public Page insertNewPage(int index);
	public Page getPage(int index);
	public int getPageCount();
	public void removePage(int index);
	public void removePage(Page page);
	public default int getColsValue() {
		if (getCols() != null) {
			return getCols().intValue();
		} else if (getParent() != null) {
			return getParent().getColsValue();
		} else {
			return 1;
		}
	}
	public default int getRowsValue() {
		if (getRows() != null) {
			return getRows().intValue();
		} else if (getParent() != null) {
			return getParent().getRowsValue();
		} else {
			return 1;
		}
	}
	public default boolean getDuplexValue() {
		if (getDuplex() != null) {
			return getDuplex().booleanValue();
		} else if (getParent() != null) {
			return getParent().getDuplexValue();
		} else {
			return false;
		}
	}
}
