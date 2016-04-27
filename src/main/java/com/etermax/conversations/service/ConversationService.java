package com.etermax.conversations.service;

import com.etermax.conversations.error.DeleteConversationException;
import com.etermax.conversations.error.GetConversationException;
import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.Conversation;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ConversationService {
	Conversation saveConversation(Set<Long> conversationUsers);

	Conversation getConversation(String conversationId) throws GetConversationException;

	List<Conversation> getUserConversations(Long userId);

	void deleteConversation(String conversationId, Long userId, String application, Date deletionDate)
			throws DeleteConversationException, ModelException;

	Conversation getConversationWithUsers(Set<Long> userIds);

	List<Conversation> getUserActiveConversations(Long userId, String application);
}
