package com.etermax.conversations.service;

import com.etermax.conversations.error.DeleteMessageException;
import com.etermax.conversations.error.GetMessageException;
import com.etermax.conversations.error.GetUserMessagesException;
import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.error.SaveMessageException;
import com.etermax.conversations.model.AddressedMessage;
import com.etermax.conversations.model.ConversationMessage;

import java.util.List;
import java.util.Map;

public interface MessageService {
	ConversationMessage saveMessage(ConversationMessage conversationMessage, String conversationId)
			throws SaveMessageException;

	void deleteMessage(String conversationId, String messageId, Long user, String app) throws DeleteMessageException, ModelException;

	List<AddressedMessage> getRetrocompatibilityUserMessages(List<Long> userIds, String dateString, String application)
			throws GetUserMessagesException;

	AddressedMessage saveRetrocompatibilityMessage(AddressedMessage addressedMessage) throws SaveMessageException;

	Map<String, AddressedMessage> getLastMessages(Long userId, List<String> conversationIds, String application)
			throws GetMessageException;

	String getMessageApplication(String conversationId, String messageId);
}
