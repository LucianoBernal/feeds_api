package com.etermax.conversations.factory;

import com.etermax.conversations.model.ConversationComparator;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.impl.ConversationServiceImpl;

public class ConversationServiceFactory {
	private ConversationRepositoryFactory conversationRepositoryFactory;
	private ConversationFactory conversationFactory;
	private UserFactory userFactory;
	private EventServiceFactory eventServiceFactory;

	public ConversationServiceFactory(ConversationRepositoryFactory conversationRepositoryFactory,
			ConversationFactory conversationFactory, UserFactory userFactory, EventServiceFactory
			eventServiceFactory) {
		this.conversationRepositoryFactory = conversationRepositoryFactory;
		this.conversationFactory = conversationFactory;
		this.userFactory = userFactory;
		this.eventServiceFactory = eventServiceFactory;
	}

	public ConversationService createConversationService() {
		ConversationComparator conversationComparator = new ConversationComparator();
		ConversationRepository conversationRepository = conversationRepositoryFactory.createRepository();
		return new ConversationServiceImpl(conversationRepository, conversationFactory, conversationComparator,
										   userFactory, eventServiceFactory.createEventService());
	}
}
