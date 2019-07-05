package org.brailleblaster.libembosser.drivers.utils.document.events;

public class BrailleEvent implements DocumentEvent {
	private String braille;
	public BrailleEvent(String braille) {
		this.braille =braille;
	}
	public String getBraille() {
		return braille;
	}
}