package com.etermax.conversations.model;

import com.etermax.conversations.error.InvalidConversation;

import java.util.Set;

//TODO: Borrar luego de la migración
public class DeletedConversation extends Conversation{

	private Long deletedBy;

	public DeletedConversation(Set<User> users, Long deletedBy) throws InvalidConversation {
		super(users);
		this.deletedBy = deletedBy;
	}

	public Long getDeletedBy() {
		return deletedBy;
	}
}
