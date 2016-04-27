package com.etermax.conversations.retrocompatibility.migration.repository;

import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MigrationRepository {
	List<Conversation> getConversations(Long userId);
	Map<Conversation, List<ConversationMessage>> getMessages(List<Conversation> conversations);
	Map<Long, Set<String>> getApplications(Set<Long> userIds);
	Boolean checkAndSetMigration(Long userId);
}
