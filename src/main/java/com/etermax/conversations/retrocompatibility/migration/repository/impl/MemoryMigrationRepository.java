package com.etermax.conversations.retrocompatibility.migration.repository.impl;

import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.ConversationTextMessage;
import com.etermax.conversations.model.User;
import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class MemoryMigrationRepository implements MigrationRepository {

	private Set<Long> migratedUsers;

	public MemoryMigrationRepository() {
		this.migratedUsers = new HashSet<>();
	}

	@Override
	public List<Conversation> getConversations(Long userId) {
		return Collections.singletonList(new Conversation(Sets.newHashSet(new User(userId), new User(userId + 1))));
	}

	@Override
	public Map<Conversation, List<ConversationMessage>> getMessages(List<Conversation> conversations) {
		Map<Conversation, List<ConversationMessage>> response = new HashMap<>();

		conversations.forEach(conversation -> {

			List<ConversationMessage> messages = conversation.getUserIds()
															 .stream()
															 .map(User::new)
															 .map(user -> createDummyMessage(conversation, user))
															 .collect(Collectors.toList());

			response.put(conversation, messages);
		});

		return null;
	}

	@Override
	public Map<Long, Set<String>> getApplications(Set<Long> userIds) {
		Map<Long, Set<String>> response = new HashMap<>();
		userIds.forEach(userId -> response.put(userId, Sets.newHashSet("CRACK_ME")));
		return response;
	}

	@Override
	public Boolean checkAndSetMigration(Long userId) {
		if (migratedUsers.contains(userId)) {
			return true;
		} else {
			migratedUsers.add(userId);
			return false;
		}
	}

	public ConversationTextMessage createDummyMessage(Conversation conversation, User user) {
		return new ConversationTextMessage(user, conversation.getId(), new Date(), "XMPPServerApi de" + user.getId(),
										   "CRACK_ME", false);
	}

}
