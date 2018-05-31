package org.brailleblaster.libembosser.pef;

public interface Volume extends RowGap, SubElement<PEFDocument>, Rows, Cols, Duplex {
	public Section appendNewSection();
	public Section insertNewSection(int index);
	public Section getSection(int index);
	public int getSectionCount();
	public void removeSection(int index);
	public void removeSection(Section section);
}
