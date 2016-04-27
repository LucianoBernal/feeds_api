package com.etermax.conversations.repository.impl.memory.domain;

import java.util.HashMap;
import java.util.Map;

public class MemoryUnreadMessages {
	private String conversationId;
	private String application;
	private Map<Long, Long> userUnreadMessages;

	public MemoryUnreadMessages(String conversationId, String application) {
		this.conversationId = conversationId;
		this.application = application;
		this.userUnreadMessages = new HashMap<>();
	}

	public void setUserUnreadMessages(Long userId, Long unreadMessages) {
		this.userUnreadMessages.put(userId, unreadMessages);
	}

	public Long getUserUnreadMessages(Long userId) {
		Long unread = userUnreadMessages.get(userId);
		return unread == null ? 0l : unread;
	}

	public String getConversationId() {
		return conversationId;
	}

	public String getApplication() {
		return application;
	}

}
