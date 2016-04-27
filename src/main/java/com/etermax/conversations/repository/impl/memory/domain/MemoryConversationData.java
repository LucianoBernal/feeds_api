package com.etermax.conversations.repository.impl.memory.domain;

import com.etermax.conversations.repository.impl.memory.filter.ConversationDataFilter;

import java.util.Date;

public interface MemoryConversationData {
	String getId();
	String getConversationId();
	String getType();
	Date getDate();
	String getApplication();
	boolean accept(ConversationDataFilter conversationDataFilter);
}
