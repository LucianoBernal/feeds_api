package com.etermax.conversations.factory;

import com.etermax.conversations.application.healthcheck.factory.ConversationRepositoryHealthCheckFactory;
import com.etermax.conversations.application.healthcheck.factory.MemoryConversationRepositoryFactoryHealthCheck;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.repository.impl.memory.MemoryConversationRepository;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MemoryConversationRepositoryFactory implements ConversationRepositoryFactory {

	@JsonProperty("max_conversations")
	private Integer maxConversations;

	@JsonProperty("max_messages")
	private Integer maxMessages;

	private ConversationRepository conversationRepository;

	public MemoryConversationRepositoryFactory(	Integer maxConversations, Integer maxMessages) {
		this.maxConversations = maxConversations;
		this.maxMessages = maxMessages;
	}

	public ConversationRepository createRepository() {
		if(conversationRepository == null){
			conversationRepository = new MemoryConversationRepository(maxConversations, maxMessages);
		}
		return conversationRepository;
	}

	@Override
	public ConversationRepositoryHealthCheckFactory createRepositoryHealthCheckFactory() {
		return new MemoryConversationRepositoryFactoryHealthCheck();
	}
}
