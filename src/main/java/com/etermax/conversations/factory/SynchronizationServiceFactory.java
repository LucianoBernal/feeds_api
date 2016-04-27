package com.etermax.conversations.factory;

import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.SynchronizationService;
import com.etermax.conversations.service.impl.SynchronizationServiceImpl;

public class SynchronizationServiceFactory {
	private ConversationRepositoryFactory conversationRepositoryFactory;

	public SynchronizationServiceFactory(ConversationRepositoryFactory conversationRepositoryFactory) {
		this.conversationRepositoryFactory = conversationRepositoryFactory;
	}

	public SynchronizationService createSynchronizationService() {
		ConversationRepository conversationRepository = conversationRepositoryFactory.createRepository();
		return new SynchronizationServiceImpl(conversationRepository);
	}
}
