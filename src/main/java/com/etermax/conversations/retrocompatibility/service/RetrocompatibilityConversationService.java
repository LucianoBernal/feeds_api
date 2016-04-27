package com.etermax.conversations.retrocompatibility.service;

import com.etermax.conversations.repository.ConversationRepository;

import java.util.List;
import java.util.Map;

public class RetrocompatibilityConversationService {
	private ConversationRepository conversationRepository;

	public RetrocompatibilityConversationService(ConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	public Map<String, Long> getUnreadMessagesCount(List<String> conversations, Long userId, String application) {
		return conversationRepository.getUnreadMessagesCount(conversations, userId, application);
	}
}
