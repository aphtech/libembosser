package org.brailleblaster.libembosser.drivers.utils;

public interface ThrowingBiConsumer<R, U, E extends Exception> {
	public void accept(R r, U u) throws E;
}
