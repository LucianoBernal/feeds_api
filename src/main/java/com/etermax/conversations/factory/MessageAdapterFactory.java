package com.etermax.conversations.factory;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.impl.MessageAdapterImpl;
import com.etermax.conversations.service.MessageService;

public class MessageAdapterFactory {
	private MessageServiceFactory messageServiceFactory;
	private ConversationMessageFactory conversationMessageFactory;
	private AddressedMessageFactory addressedMessageFactory;
	private UserFactory userFactory;

	public MessageAdapterFactory(MessageServiceFactory messageServiceFactory,
			ConversationMessageFactory conversationMessageFactory, AddressedMessageFactory addressedMessageFactory,
			UserFactory userFactory) {
		this.messageServiceFactory = messageServiceFactory;
		this.conversationMessageFactory = conversationMessageFactory;
		this.addressedMessageFactory = addressedMessageFactory;
		this.userFactory = userFactory;
	}

	public MessageAdapter createMessageAdapter(){
		MessageService messageService = messageServiceFactory.createMessageService();
		return new MessageAdapterImpl(messageService, conversationMessageFactory, addressedMessageFactory, userFactory);
	}
}
