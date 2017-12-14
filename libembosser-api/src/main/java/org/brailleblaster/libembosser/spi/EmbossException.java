package org.brailleblaster.libembosser.spi;

public class EmbossException extends Exception {
	
	public EmbossException() {
	}

	public EmbossException(String message) {
		super(message);
	}

	public EmbossException(Throwable cause) {
		super(cause);
	}

	public EmbossException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmbossException(String message, Throwable cause, boolean enableSuppression, boolean writeableStack) {
		super(message, cause, enableSuppression, writeableStack);
	}

}
