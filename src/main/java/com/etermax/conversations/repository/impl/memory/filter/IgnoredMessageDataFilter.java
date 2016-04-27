package com.etermax.conversations.repository.impl.memory.filter;

import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationData;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationEvent;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationMessage;

import java.util.List;
import java.util.stream.Collectors;

public class IgnoredMessageDataFilter implements ConversationDataFilter {
	private Long userId;

	public IgnoredMessageDataFilter(Long userId) {
		this.userId = userId;
	}

	@Override
	public List<MemoryConversationData> filter(List<MemoryConversationData> conversationDataList) {
		return conversationDataList.stream().filter(conversationData -> conversationData.accept(this))
				.collect(Collectors.toList());
	}

	@Override
	public boolean filter(MemoryConversationMessage conversationMessage) {
		return ! conversationMessage.getIgnoredBy().stream().anyMatch(user -> user.equals(userId));
	}

	@Override
	public boolean filter(MemoryConversationEvent conversationEvent) {
		return true;
	}
}
