package com.etermax.conversations.retrocompatibility.migration.repository.factory;

import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;

import java.util.*;

public class OffMigrationRepositoryFactory implements MigrationRepositoryFactory {
	@Override
	public MigrationRepository createRepository() {
		return new MigrationRepository() {
			@Override
			public List<Conversation> getConversations(Long userId) {
				return new ArrayList<>();
			}

			@Override
			public Map<Conversation, List<ConversationMessage>> getMessages(List<Conversation> conversations) {
				return new HashMap<>();
			}

			@Override
			public Map<Long, Set<String>> getApplications(Set<Long> userIds) {
				return new HashMap<>();
			}

			@Override
			public Boolean checkAndSetMigration(Long userId) {
				return true;
			}
		};
	}
}
