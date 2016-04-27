package com.etermax.conversations.service;

import com.etermax.conversations.error.GetConversationMessagesException;
import com.etermax.conversations.model.ConversationHistory;
import com.etermax.conversations.model.ConversationSync;
import com.etermax.conversations.model.Range;

import java.util.List;

public interface SynchronizationService {
	ConversationHistory getConversationHistory(String conversationId, Range range, Long userId, String application) throws GetConversationMessagesException;

	List<ConversationSync> getConversationSync(Long userId, String dateString, String application);
}
