package com.etermax.conversations.error;

public class ServerException extends RuntimeException {
	private Throwable cause;

	public ServerException(Throwable cause, String message) {
		super(message);
		this.cause = cause;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}
}
