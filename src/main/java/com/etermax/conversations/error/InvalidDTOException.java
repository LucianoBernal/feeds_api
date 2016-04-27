package com.etermax.conversations.error;

public class InvalidDTOException extends Exception {
	private Throwable cause;

	public InvalidDTOException(Throwable cause, String message) {
		super(message);
		this.cause = cause;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}
}
