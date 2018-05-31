package org.brailleblaster.libembosser.pef;

public interface Row extends InheritingRowGap<Page> {
	public String getBraille();
	public void setBraille(String braille);
}
