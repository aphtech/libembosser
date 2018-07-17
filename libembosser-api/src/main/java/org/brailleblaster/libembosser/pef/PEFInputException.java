package org.brailleblaster.libembosser.pef;

/** Exception indicating a problem when reading a PEF.
 * 
 * @author Michael Whapples
 *
 */
public class PEFInputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PEFInputException() {
	}

	public PEFInputException(String arg0) {
		super(arg0);
	}

	public PEFInputException(Throwable arg0) {
		super(arg0);
	}

	public PEFInputException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PEFInputException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
