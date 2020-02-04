package it.polito.dp2.BIB.ass3;

public class DestroyedBookshelfException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DestroyedBookshelfException() {
	}

	public DestroyedBookshelfException(String message) {
		super(message);
	}

	public DestroyedBookshelfException(Throwable cause) {
		super(cause);
	}

	public DestroyedBookshelfException(String message, Throwable cause) {
		super(message, cause);
	}

	public DestroyedBookshelfException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
