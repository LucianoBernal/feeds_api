package com.etermax.conversations.model;

import com.etermax.conversations.error.ModelException;

import java.util.Date;

public class IndividualMessageReceipt {
	private ReceiptType type;
	private Date date;
	private Long userId;

	public IndividualMessageReceipt(ReceiptType type, Long user) throws ModelException {
		validate(type, user);
		this.type = type;
		this.date = new Date();
		this.userId = user;
	}

	private void validate(ReceiptType type, Long user) throws ModelException {
		if(type == null){
			throw new ModelException(null);
		}
		if(user == null || user.equals(0l)){
			throw new ModelException(null);
		}
	}

	public ReceiptType getType() {
		return type;
	}
	public Date getDate() {
		return date;
	}
	public Long getUser() {
		return userId;
	}

	public void accept(ReadResetter visitor, Long conversationId) {
		type.accept(visitor);
	}
}
