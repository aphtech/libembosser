package org.brailleblaster.libembosser.pef;

/** Exception indicating a problem when outputting a PEF document.
 * 
 * @author Michael Whapples
 *
 */
public class PEFOutputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PEFOutputException() {
		super();
	}

	public PEFOutputException(String arg0) {
		super(arg0);
	}

	public PEFOutputException(Throwable arg0) {
		super(arg0);
	}

	public PEFOutputException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PEFOutputException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
