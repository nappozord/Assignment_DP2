package it.polito.dp2.BIB.ass3;

public class TooManyItemsException extends Exception {

	public TooManyItemsException() {
	}

	public TooManyItemsException(String message) {
		super(message);
	}

	public TooManyItemsException(Throwable cause) {
		super(cause);
	}

	public TooManyItemsException(String message, Throwable cause) {
		super(message, cause);
	}

	public TooManyItemsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
