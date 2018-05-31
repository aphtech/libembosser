package org.brailleblaster.libembosser.pef;

public interface Page extends InheritingRowGap<Section> {
	public Row appendNewRow();
	public Row insertNewRow(int index);
	public Row getRow(int index);
	public int getRowCount();
	public void removeRow(int index);
	public void removeRow(Row row);
	public Row appendRow(String brl);
	public Row insertRow(int index, String brl);
}
