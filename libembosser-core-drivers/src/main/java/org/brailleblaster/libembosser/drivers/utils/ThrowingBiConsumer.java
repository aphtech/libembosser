package org.brailleblaster.libembosser.drivers.utils;

public interface ThrowingBiConsumer<R, U, E extends Exception> {
	void accept(R r, U u) throws E;
}
