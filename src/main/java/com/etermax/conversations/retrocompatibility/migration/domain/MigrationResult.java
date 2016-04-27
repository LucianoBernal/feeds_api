package com.etermax.conversations.retrocompatibility.migration.domain;

import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;

import java.util.List;

public class MigrationResult {

	private Conversation conversation;
	private List<ConversationMessage> messagesMigrated;
	private Boolean migrated;

	public MigrationResult(Conversation conversation, List<ConversationMessage> messagesMigrated, Boolean migrated) {
		this.conversation = conversation;
		this.messagesMigrated = messagesMigrated;
		this.migrated = migrated;
	}

	public Conversation getConversation() {
		return conversation;
	}

	public List<ConversationMessage> getMessagesMigrated() {
		return messagesMigrated;
	}

	public String getConversationId() {
		return conversation.getId();
	}

	public Long getMessagesMigratedCount() {
		return (long) messagesMigrated.size();
	}

	public Boolean hasBeenMigrated() {
		return migrated;
	}
}
