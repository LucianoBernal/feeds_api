package com.etermax.conversations.repository;

import com.etermax.conversations.model.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConversationRepository {
	Conversation saveConversation(Conversation conversation);

	List<Conversation> saveConversations(List<Conversation> conversations);

	ConversationMessage saveMessage(ConversationMessage conversationMessage, Conversation conversation);

	Conversation getConversationWithId(String conversationId);

	Conversation getConversationWithUsers(Set<Long> userIds);

	List<Conversation> getUserConversations(Long userId);

	List<Conversation> getUserActiveConversations(Long userId, String application);

	List<AddressedMessage> getAddressedMessages(List<Long> userIds, Date date, String application);

	void deleteMessage(String conversationId, String messageId, Long userId);

	ConversationHistory getConversationHistory(String conversationId, Range range, Long userId, String application);

	void deleteConversation(String conversationId, Long userId, Date deletionDate, String app);

	ConversationMessage getConversationMessage(String conversationId, String messageId, Long userId);

	void resetRead(String conversationId, String application, Long userId);

	IndividualMessageReceipt saveReceiptInMessage(String conversationId, String messageId,
			IndividualMessageReceipt receipt);

	void saveEvent(Event event);

	List<ConversationSync> getConversationSyncData(Long userId, List<String> conversationIds, Date date, String application);

	void clearRepository();

	void flush();

	Map<String, AddressedMessage> getLastMessages(Long userId, List<String> conversationIds, String application);

	List<ConversationMessage> saveMessages(List<ConversationMessage> messages);

	boolean isAlreadyAcknowledged(String conversationId, String messageId, IndividualMessageReceipt receipt);

	String getMessageApplication(String conversationId, String messageId);

	Map<String,Long> getUnreadMessagesCount(List<String> conversations, Long userId, String application);

	String createConversationId(Conversation conversation);
}
