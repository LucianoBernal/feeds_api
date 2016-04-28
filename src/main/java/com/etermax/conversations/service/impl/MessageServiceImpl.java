package com.etermax.conversations.service.impl;

import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.ConversationMessageFactory;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.Event;
import com.etermax.conversations.model.EventData;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.EventService;
import com.etermax.conversations.service.MessageService;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class MessageServiceImpl implements MessageService {
	private final ConversationMessageFactory conversationMessageFactory;
	private ConversationRepository conversationRepository;
	private ConversationService conversationService;
	private EventService eventService;

	public MessageServiceImpl(ConversationRepository conversationRepository,
			ConversationMessageFactory conversationMessageFactory, ConversationService conversationService,
			EventService eventService) {
		this.conversationRepository = conversationRepository;
		this.conversationMessageFactory = conversationMessageFactory;
		this.conversationService = conversationService;
		this.eventService = eventService;
	}

	@Override
	public ConversationMessage saveMessage(ConversationMessage conversationMessage, String conversationId)
			throws SaveMessageException {
		try {
			Conversation conversation = conversationRepository.getConversationWithId(conversationId);
			checkUserIsInConversation(conversationMessage.getSender().getId(), conversation);
			ConversationMessage messageSent = conversationRepository.saveMessage(conversationMessage, conversation);
			return messageSent;
		} catch (ConversationNotFoundException | UserNotInConversationException e) {
			throw new SaveMessageException(e);
		}
	}

	private void checkUserIsInConversation(Long id, Conversation conversation) {
		if (!conversation.getUserIds().contains(id)) {
			throw new UserNotInConversationException();
		}
	}

	@Override
	public String getMessageApplication(String conversationId, String messageId) {
		return conversationRepository.getMessageApplication(conversationId, messageId);
	}

	private Conversation getOrCreateConversationWithUsers(Set<Long> userIds) {
		try {
			return conversationRepository.getConversationWithUsers(userIds);
		} catch (ConversationNotFoundException e) {
			return conversationService.saveConversation(userIds);
		}
	}

	@Override
	public void deleteMessage(String conversationId, String messageId, Long userId, String app)
			throws DeleteMessageException, ModelException {
		try {
			Long messageDate = conversationRepository.getConversationMessage(conversationId, messageId, userId)
													 .getDate()
													 .getTime();
			conversationRepository.deleteMessage(conversationId, messageId, userId);

			Event event = new Event("DELETE_MESSAGE", Arrays.asList(new EventData("messageId", messageId),
																	new EventData("messageDate",
																				  messageDate.toString())),
									conversationId, userId, new Date(), app);
			eventService.registerEvent(event);

		} catch (ConversationNotFoundException | MessageNotFoundException e) {
			throw new DeleteMessageException(e);
		}
	}

	private Date getDate(String dateString) throws GetUserMessagesException {
		if (dateString == null) {
			return new Date(0L);
		}
		try {
			Long time = Long.parseLong(dateString);
			return new Date(time);
		} catch (NumberFormatException e) {
			throw new GetUserMessagesException(e);
		}
	}
}
