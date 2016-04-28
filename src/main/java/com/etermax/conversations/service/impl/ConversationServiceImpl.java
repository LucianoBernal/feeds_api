package com.etermax.conversations.service.impl;

import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.ConversationFactory;
import com.etermax.conversations.factory.UserFactory;
import com.etermax.conversations.model.*;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.EventService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConversationServiceImpl implements ConversationService {
	private ConversationRepository conversationRepository;
	private ConversationFactory conversationFactory;
	private ConversationComparator conversationComparator;
	private UserFactory userFactory;
	private EventService eventService;

	public ConversationServiceImpl(ConversationRepository conversationRepository,
			ConversationFactory conversationFactory, ConversationComparator conversationComparator,
			UserFactory userFactory, EventService eventService) {
		this.conversationRepository = conversationRepository;
		this.conversationFactory = conversationFactory;
		this.conversationComparator = conversationComparator;
		this.userFactory = userFactory;
		this.eventService = eventService;
	}

	@Override
	public Conversation saveConversation(Set<Long> userIds) {
		Set<User> users = userIds.stream().map(this::createUser).collect(Collectors.toSet());
		if (users.size() == 2) { //TODO: add type
			try {
				return conversationRepository.getConversationWithUsers(userIds);
			} catch (ConversationNotFoundException e) {
				return createAndSaveConversation(users);
			}
		} else {
			return createAndSaveConversation(users);
		}
	}

	@Override
	public List<Conversation> getUserConversations(Long userId) {
		List<Conversation> userConversations = conversationRepository.getUserConversations(userId); //getUserActiveConversations, FIXME
		userConversations.sort(conversationComparator);
		return userConversations;
	}

	@Override
	public Conversation getConversation(String conversationId) throws GetConversationException {
		try {
			return conversationRepository.getConversationWithId(conversationId);
		} catch (ConversationNotFoundException e) {
			throw new GetConversationException(e);
		}
	}

	@Override
	public void deleteConversation(String conversationId, Long userId, String application, Date deletionDate)
			throws DeleteConversationException, ModelException {
		try {
			Conversation conversation = conversationRepository.getConversationWithId(conversationId);
			checkUserIsInConversation(userId, conversation);
			conversationRepository.deleteConversation(conversationId, userId, deletionDate, application);
			conversationRepository.resetRead(conversationId, application, userId);
			EventData appData = new EventData("application", application);
			Event event = new Event("DELETE_CONVERSATION", Arrays.asList(appData), conversationId, userId, deletionDate,
					application);
			eventService.registerEvent(event);

		} catch (ConversationNotFoundException | UserNotInConversationException e) {
			throw new DeleteConversationException(e);
		}
	}

	@Override
	public Conversation getConversationWithUsers(Set<Long> userIds) {
		try {
			return conversationRepository.getConversationWithUsers(userIds);
		}catch (ConversationNotFoundException e){
			throw new DeleteConversationException(e);
		}
	}

	@Override
	public List<Conversation> getUserActiveConversations(Long userId, String application) {
		return conversationRepository.getUserActiveConversations(userId, application);
	}

	private void checkUserIsInConversation(Long userId, Conversation conversation) {
		if (!conversation.getUserIds().contains(userId)) {
			throw new UserNotInConversationException();
		}
	}

	private Conversation createAndSaveConversation(Set<User> users) {
		Conversation conversation;
		try {
			conversation = conversationFactory.createConversation(users);
		} catch (InvalidConversation e) {
			throw new ServerException(e, "Unable to create conversation");
		}
		return conversationRepository.saveConversation(conversation);
	}

	private User createUser(Long userId) {
		User user;
		try {
			user = userFactory.createUser(userId);
		} catch (InvalidUserException e) {
			throw new ServerException(e, "Error creating user");
		}
		return user;
	}

}
