package com.etermax.conversations.service;

import com.etermax.conversations.error.DeleteMessageException;
import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.error.SaveMessageException;
import com.etermax.conversations.model.ConversationMessage;

public interface MessageService {
	ConversationMessage saveMessage(ConversationMessage conversationMessage, String conversationId)
			throws SaveMessageException;

	void deleteMessage(String conversationId, String messageId, Long user, String app) throws DeleteMessageException, ModelException;

	String getMessageApplication(String conversationId, String messageId);
}
