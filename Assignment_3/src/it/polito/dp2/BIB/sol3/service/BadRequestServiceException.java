package it.polito.dp2.BIB.sol3.service;

public class BadRequestServiceException extends Exception {

	public BadRequestServiceException() {
	}

	public BadRequestServiceException(String message) {
		super(message);
	}

	public BadRequestServiceException(Throwable cause) {
		super(cause);
	}

	public BadRequestServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadRequestServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
