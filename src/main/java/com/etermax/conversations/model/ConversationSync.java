package com.etermax.conversations.model;

import java.util.List;

public class ConversationSync {
	private String conversationId;
	private List<ConversationData> conversationDataList;
	private HasMore hasMore;
	private Long unreadMessages;

	public ConversationSync(String conversationId, List<ConversationData> conversationDataList, HasMore hasMore,
			Long unreadMessages) {
		this.conversationId = conversationId;
		this.conversationDataList = conversationDataList;
		this.hasMore = hasMore;
		this.unreadMessages = unreadMessages;
	}

	public String getConversationId() {
		return conversationId;
	}

	public List<ConversationData> getConversationDataList() {
		return conversationDataList;
	}

	public HasMore getHasMore() {
		return hasMore;
	}

	public Long getUnreadMessages() {
		if (unreadMessages == null) {
			return 0l;
		} else {
			return unreadMessages;
		}
	}
}
