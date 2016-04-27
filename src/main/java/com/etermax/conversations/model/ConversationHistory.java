package com.etermax.conversations.model;

import java.util.List;

public class ConversationHistory {
	private HasMore hasMore;
	private List<ConversationData> conversationDataList;

	public ConversationHistory(List<ConversationData> conversationDataList, HasMore hasMore) {
		this.conversationDataList = conversationDataList;
		this.hasMore = hasMore;
	}

	public HasMore getHasMore() {
		return hasMore;
	}

	public List<ConversationData> getConversationDataList() {
		return conversationDataList;
	}

}
