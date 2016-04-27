package com.etermax.conversations.repository.impl.memory.domain;

import java.util.Date;

public class MemoryConversationDeletion {

	private String conversationId;
	private Long userId;
	private String app;
	private Date deletionDate;

	public MemoryConversationDeletion(String conversationId, Long userId, String app, Date deletionDate) {
		this.conversationId = conversationId;
		this.userId = userId;
		this.app = app;
		this.deletionDate = deletionDate;
	}

	public String getConversationId() {
		return conversationId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getApp() {
		return app;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}
}
