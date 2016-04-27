package com.etermax.conversations.factory;

import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.ReceiptService;
import com.etermax.conversations.service.impl.ReceiptServiceImpl;

public class ReceiptServiceFactory {
	private ConversationRepositoryFactory conversationRepositoryFactory;

	public ReceiptServiceFactory(ConversationRepositoryFactory conversationRepositoryFactory) {
		this.conversationRepositoryFactory = conversationRepositoryFactory;
	}

	public ReceiptService createReceiptService() {
		ConversationRepository conversationRepository = conversationRepositoryFactory.createRepository();
		return new ReceiptServiceImpl(conversationRepository);
	}
}
