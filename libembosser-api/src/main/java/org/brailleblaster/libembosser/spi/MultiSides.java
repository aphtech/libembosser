package org.brailleblaster.libembosser.spi;

@Deprecated
public enum MultiSides {
	INTERPOINT(true), P1ONLY(false), P2ONLY(false),
	SADDLE_STITCH_SINGLE_SIDED(false), SADDLE_STITCH_DOUBLE_SIDED(true),
	Z_FOLDING_SINGLE_HORIZONTAL(false), Z_FOLDING_DOUBLE_HORIZONTAL(true),
	Z_FOLDING_SINGLE_VERTICAL(false), Z_FOLDING_DOUBLE_VERTICAL(true);
	private boolean doubleSide;
	private MultiSides(boolean doubleSide) {
		this.doubleSide = doubleSide;
	}
	public boolean isDoubleSide() {
		return doubleSide;
	}
}
