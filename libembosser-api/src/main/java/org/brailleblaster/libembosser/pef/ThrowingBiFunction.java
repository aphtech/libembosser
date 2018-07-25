package org.brailleblaster.libembosser.pef;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Exception> {
	public R apply(T arg1, U arg2) throws E;
}
