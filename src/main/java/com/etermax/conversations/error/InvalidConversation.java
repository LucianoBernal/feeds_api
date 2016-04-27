package com.etermax.conversations.error;

public class InvalidConversation extends ModelException {
	private Throwable cause;

	public InvalidConversation(Throwable cause) {
		super(cause);
	}

	@Override
	public Throwable getCause() {
		return cause;
	}
}
