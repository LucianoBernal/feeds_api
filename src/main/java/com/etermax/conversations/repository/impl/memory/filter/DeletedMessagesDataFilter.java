package com.etermax.conversations.repository.impl.memory.filter;

import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationData;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationEvent;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationMessage;

import java.util.List;
import java.util.stream.Collectors;

public class DeletedMessagesDataFilter implements ConversationDataFilter {
	private Long userId;

	public DeletedMessagesDataFilter(Long userId) {
		this.userId = userId;
	}

	@Override
	public List<MemoryConversationData> filter(List<MemoryConversationData> conversationDataList) {
		return conversationDataList.stream().filter(conversationData -> conversationData.accept(this))
				.collect(Collectors.toList());
	}

	public boolean filter(MemoryConversationMessage conversationMessage) {
		return ! conversationMessage.getDeletedBy().stream().anyMatch(user -> user.equals(userId));
	}

	public boolean filter(MemoryConversationEvent conversationEvent) {
		return true;
	}

}
