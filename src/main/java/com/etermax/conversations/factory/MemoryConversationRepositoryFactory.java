package com.etermax.conversations.factory;

import com.etermax.conversations.application.healthcheck.factory.ConversationRepositoryHealthCheckFactory;
import com.etermax.conversations.application.healthcheck.factory.MemoryConversationRepositoryFactoryHealthCheck;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.repository.impl.memory.MemoryConversationRepository;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MemoryConversationRepositoryFactory implements ConversationRepositoryFactory {

	private AddressedMessageFactory addressedMessageFactory;

	@JsonProperty("max_conversations")
	private Integer maxConversations;

	@JsonProperty("max_messages")
	private Integer maxMessages;

	private ConversationRepository conversationRepository;

	public MemoryConversationRepositoryFactory() {
		this.addressedMessageFactory = new AddressedMessageFactory();
	}

	public MemoryConversationRepositoryFactory(AddressedMessageFactory addressedMessageFactory,
			Integer maxConversations, Integer maxMessages) {
		this.maxConversations = maxConversations;
		this.maxMessages = maxMessages;
		this.addressedMessageFactory = addressedMessageFactory;
	}

	public ConversationRepository createRepository() {
		if(conversationRepository == null){
			conversationRepository = new MemoryConversationRepository(addressedMessageFactory, maxConversations, maxMessages);
		}
		return conversationRepository;
	}

	@Override
	public ConversationRepositoryHealthCheckFactory createRepositoryHealthCheckFactory() {
		return new MemoryConversationRepositoryFactoryHealthCheck();
	}
}
