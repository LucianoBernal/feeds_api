package com.etermax.conversations.repository.impl.memory.filter;

import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationData;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationEvent;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationMessage;

import java.util.List;
import java.util.stream.Collectors;

public class RetrocompatibilityDataFilter implements ConversationDataFilter {
	@Override
	public List<MemoryConversationData> filter(List<MemoryConversationData> conversationDataList) {
		return conversationDataList.stream().filter(conversationData -> conversationData.accept(this))
				.collect(Collectors.toList());
	}

	@Override
	public boolean filter(MemoryConversationMessage conversationMessage) {
		return true;
	}

	@Override
	public boolean filter(MemoryConversationEvent conversationEvent) {
		return false;
	}
}
