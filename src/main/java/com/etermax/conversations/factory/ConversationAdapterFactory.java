package com.etermax.conversations.factory;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.impl.ConversationAdapterImpl;
import com.etermax.conversations.service.ConversationService;

public class ConversationAdapterFactory {
	private ConversationServiceFactory conversationServiceFactory;

	public ConversationAdapterFactory(ConversationServiceFactory conversationServiceFactory) {
		this.conversationServiceFactory = conversationServiceFactory;
	}

	public ConversationAdapter createAdapter() {
		ConversationService conversationService = conversationServiceFactory.createConversationService();
		return new ConversationAdapterImpl(conversationService);
	}
}
