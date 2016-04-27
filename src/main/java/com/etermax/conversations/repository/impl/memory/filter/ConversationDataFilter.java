package com.etermax.conversations.repository.impl.memory.filter;

import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationData;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationEvent;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationMessage;

import java.util.List;

public interface ConversationDataFilter {

	List<MemoryConversationData> filter(List<MemoryConversationData> conversationDataList);
	boolean filter(MemoryConversationMessage conversationMessage);
	boolean filter(MemoryConversationEvent conversationEvent);

}
