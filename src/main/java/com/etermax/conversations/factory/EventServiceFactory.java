package com.etermax.conversations.factory;

import com.etermax.conversations.service.EventService;
import com.etermax.conversations.service.impl.EventServiceImpl;

public class EventServiceFactory {

	ConversationRepositoryFactory conversationRepositoryFactory;

	public EventServiceFactory(ConversationRepositoryFactory conversationRepositoryFactory) {
		this.conversationRepositoryFactory = conversationRepositoryFactory;

	}

	public EventService createEventService() {
		return new EventServiceImpl(conversationRepositoryFactory.createRepository());
	}
}
