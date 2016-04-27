package com.etermax.conversations.service.impl;

import com.etermax.conversations.error.*;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationHistory;
import com.etermax.conversations.model.ConversationSync;
import com.etermax.conversations.model.Range;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.SynchronizationService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SynchronizationServiceImpl implements SynchronizationService {
	ConversationRepository conversationRepository;

	public SynchronizationServiceImpl(ConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	@Override
	public ConversationHistory getConversationHistory(String conversationId, Range range, Long userId, String application)
			throws GetConversationMessagesException {
		try {
			Conversation conversation = conversationRepository.getConversationWithId(conversationId);
			checkUserIsInConversation(userId, conversation);
			return conversationRepository.getConversationHistory(conversationId, range, userId, application);
		} catch (ConversationNotFoundException | UserNotInConversationException e) {
			throw new GetConversationMessagesException(e);
		}
	}

	private void checkUserIsInConversation(Long userId, Conversation conversation) {
		if (!conversation.getUserIds().contains(userId)) {
			throw new UserNotInConversationException();
		}
	}

	@Override
	public List<ConversationSync> getConversationSync(Long userId, String dateString, String application) {
		Date date;
		try {
			date = getDate(dateString);
		} catch (NumberFormatException e) {
			throw new GetUserDataException(e);
		}
		List<String> conversationIds = conversationRepository.getUserConversations(userId).stream()
				.map(Conversation::getId).collect(Collectors.toList());
		try {
			return conversationIds.isEmpty() ?
					Arrays.asList() :
					conversationRepository.getConversationSyncData(userId, conversationIds, date, application);
		} catch (ModelException e) {
			throw new ServerException(e, "");
		}

	}

	private Date getDate(String dateString) throws NumberFormatException {
		if (dateString == null) {
			return new Date(0L);
		}
		Long time = Long.parseLong(dateString);
		return new Date(time);
	}
}
