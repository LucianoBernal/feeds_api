package com.etermax.conversations.service.impl;

import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.ConversationMessageFactory;
import com.etermax.conversations.model.*;
import com.etermax.conversations.notification.model.MessageNotification;
import com.etermax.conversations.notification.service.NotificationService;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityMessageService;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.EventService;
import com.etermax.conversations.service.MessageService;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class MessageServiceImpl implements MessageService {
	private final ConversationMessageFactory conversationMessageFactory;
	private ConversationRepository conversationRepository;
	private ConversationService conversationService;
	private EventService eventService;
	private NotificationService notificationService;
	private RetrocompatibilityMessageService retrocompatibilityMessageService;

	public MessageServiceImpl(ConversationRepository conversationRepository,
			ConversationMessageFactory conversationMessageFactory, ConversationService conversationService,
			EventService eventService, NotificationService notificationService,
			RetrocompatibilityMessageService retrocompatibilityMessageService) {
		this.conversationRepository = conversationRepository;
		this.conversationMessageFactory = conversationMessageFactory;
		this.conversationService = conversationService;
		this.eventService = eventService;
		this.notificationService = notificationService;
		this.retrocompatibilityMessageService = retrocompatibilityMessageService;
	}

	@Override
	public ConversationMessage saveMessage(ConversationMessage conversationMessage, String conversationId)
			throws SaveMessageException {
		try {
			Conversation conversation = conversationRepository.getConversationWithId(conversationId);
			checkUserIsInConversation(conversationMessage.getSender().getId(), conversation);
			ConversationMessage messageSent = conversationRepository.saveMessage(conversationMessage, conversation);
			sendNewMessageNotification(conversation, messageSent);
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
	public AddressedMessage saveRetrocompatibilityMessage(AddressedMessage addressedMessage)
			throws SaveMessageException {
		Set<Long> participants = new HashSet<>();
		participants.add(addressedMessage.getSender().getId());
		Long receiver = addressedMessage.getUser().getId();
		participants.add(receiver);
		Conversation conversation = getOrCreateConversationWithUsers(participants);
		User sender = addressedMessage.getSender();
		try {
			ConversationMessage conversationMessage = conversationMessageFactory.createTextConversationMessage(
					addressedMessage.getText(), sender, conversation.getId(), addressedMessage.getApplication(),
					addressedMessage.getBlocked());
			ConversationMessage savedConversationMessage = saveMessage(conversationMessage, conversation.getId());
			addressedMessage.setId(savedConversationMessage.getId());
			retrocompatibilityMessageService.sendRetrocompatibilityMessage(savedConversationMessage, receiver);
			return addressedMessage;
		} catch (ModelException e) {
			throw new ServerException(e, "Unable to create message.");
		}
	}

	@Override
	public Map<String, AddressedMessage> getLastMessages(Long userId, List<String> conversationIds, String application)
			throws GetMessageException {
		try {
			return conversationRepository.getLastMessages(userId, conversationIds, application);
		} catch (ConversationNotFoundException e) {
			throw new GetMessageException(e);
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

	@Override
	public List<AddressedMessage> getRetrocompatibilityUserMessages(List<Long> userIds, String dateString,
			String application) throws GetUserMessagesException {
		Date date = getDate(dateString);
		try {
			String conversationId = conversationRepository.getConversationWithUsers(Sets.newHashSet(userIds)).getId();
			conversationRepository.resetRead(conversationId, application, userIds.get(0));
			return conversationRepository.getAddressedMessages(userIds, date, application)
										 .stream()
										 .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
										 .collect(Collectors.toList());
		} catch (MessageNotFoundException e) {
			throw new GetUserMessagesException(e);
		} catch (ConversationNotFoundException e){
			return new ArrayList<>();
		}
	}

	public void sendNewMessageNotification(Conversation conversation, ConversationMessage message) {
		if (!message.getIgnored()) {
			notificationService.send(new MessageNotification(conversation, message));
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
