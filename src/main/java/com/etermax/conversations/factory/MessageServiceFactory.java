package com.etermax.conversations.factory;

import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityMessageService;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.EventService;
import com.etermax.conversations.service.MessageService;
import com.etermax.conversations.service.impl.MessageServiceImpl;

public class MessageServiceFactory {
	private ConversationMessageFactory conversationMessageFactory;
	private ConversationRepositoryFactory conversationRepositoryFactory;
	private ConversationServiceFactory conversationServiceFactory;
	private EventServiceFactory eventServiceFactory;
	private NotificationServiceFactory notificationServiceFactory;
	private RetrocompatibilityMessageService retrocompatibilityMessageService;

	public MessageServiceFactory(ConversationMessageFactory conversationMessageFactory,
			ConversationRepositoryFactory conversationRepositoryFactory,
			ConversationServiceFactory conversationServiceFactory, EventServiceFactory eventServiceFactory,
			NotificationServiceFactory notificationServiceFactory,
			RetrocompatibilityMessageService retrocompatibilityMessageService) {
		this.conversationMessageFactory = conversationMessageFactory;
		this.conversationRepositoryFactory = conversationRepositoryFactory;
		this.conversationServiceFactory = conversationServiceFactory;
		this.eventServiceFactory = eventServiceFactory;
		this.notificationServiceFactory = notificationServiceFactory;
		this.retrocompatibilityMessageService = retrocompatibilityMessageService;
	}

	public MessageService createMessageService() {
		ConversationService conversationService = conversationServiceFactory.createConversationService();
		ConversationRepository conversationRepository = conversationRepositoryFactory.createRepository();
		EventService eventService = eventServiceFactory.createEventService();
		return new MessageServiceImpl(conversationRepository, conversationMessageFactory, conversationService, eventService, notificationServiceFactory.createNotificationService(),
				retrocompatibilityMessageService);
	}

}
