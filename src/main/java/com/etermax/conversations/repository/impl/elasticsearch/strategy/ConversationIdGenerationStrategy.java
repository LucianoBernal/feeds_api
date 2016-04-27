package com.etermax.conversations.repository.impl.elasticsearch.strategy;

import com.etermax.conversations.model.Conversation;

public interface ConversationIdGenerationStrategy {

	String generateId(Conversation conversation);

}
