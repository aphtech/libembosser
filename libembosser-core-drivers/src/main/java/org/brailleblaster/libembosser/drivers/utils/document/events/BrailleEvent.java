package org.brailleblaster.libembosser.drivers.utils.document.events;

public class BrailleEvent implements DocumentEvent {
	private String braille;
	public BrailleEvent(String braille) {
		this.braille =braille;
	}
	public String getBraille() {
		return braille;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((braille == null) ? 0 : braille.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BrailleEvent other = (BrailleEvent) obj;
		if (braille == null) {
			return other.braille == null;
		} else return braille.equals(other.braille);
	}
}