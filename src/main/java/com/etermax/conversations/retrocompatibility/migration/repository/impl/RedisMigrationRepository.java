package com.etermax.conversations.retrocompatibility.migration.repository.impl;

import com.etermax.conversations.model.*;
import com.etermax.conversations.retrocompatibility.migration.dao.RedisMigrationDao;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationApplication;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationMessage;
import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.etermax.vedis.connection.VedisConnectionConfiguration;
import com.etermax.vedis.connection.VedisConnectionManager;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;
import com.etermax.vedis.executor.SingleOperationExecutor;
import com.etermax.vedis.executor.bulk.BulkExecutor;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class RedisMigrationRepository implements MigrationRepository {

	private RedisMigrationDao dao;

	public RedisMigrationRepository(VedisConnectionConfiguration configuration, Long daysThreshold)
			throws InvalidConnectionDataException {
		VedisConnectionManager connectionManager = new VedisConnectionManager(configuration);
		this.dao = new RedisMigrationDao(new SingleOperationExecutor(connectionManager),
										 new BulkExecutor(connectionManager), daysThreshold);
	}

	@Override
	public List<Conversation> getConversations(Long userId) {
		return createConversations(userId, dao.getConversationInterlocutors(userId));
	}

	@Override
	public Map<Conversation, List<ConversationMessage>> getMessages(List<Conversation> conversations) {
		Map<Conversation, List<MigrationMessage>> conversationsMessages = dao.getConversationMessages(conversations);
		Map<Conversation, List<ConversationMessage>> response = new HashMap<>();

		conversationsMessages.entrySet().stream().forEach(entry -> {
			Conversation conversation = entry.getKey();
			response.put(conversation, mapMessages(conversation, entry.getValue()));
		});

		return response;
	}

	@Override
	public Map<Long, Set<String>> getApplications(Set<Long> userIds) {
		Map<Long, Set<String>> result = new HashMap<>();
		Map<Long, Set<MigrationApplication>> applicationsByUser = dao.getApplications(new ArrayList<>(userIds));

		applicationsByUser.entrySet().stream().forEach(entry -> {
			Long userId = entry.getKey();
			Set<String> userApplications = mapApplications(entry.getValue());
			result.put(userId, userApplications);
		});

		return result;
	}

	@Override
	public Boolean checkAndSetMigration(Long userId) {
		return dao.setUserMigrated(userId).equals(0L);
	}

	private List<Conversation> createConversations(Long userId, Set<Long> interlocutors) {
		Map<Long, Set<Long>> interlocutorConversations = getInterlocutorConversationList(interlocutors);

		return interlocutors.stream()
							.map(interlocutorId -> toConversation(userId, interlocutorId,
																  !interlocutorConversations.get(interlocutorId)
																						   .contains(userId)))
							.collect(Collectors.toList());
	}

	private Map<Long, Set<Long>> getInterlocutorConversationList(Set<Long> interlocutors) {
		return dao.getConversationInterlocutors(interlocutors);
	}

	private Conversation toConversation(Long userId, Long userId2, boolean deleted) {
		try {
			if(!deleted){
				return new Conversation(Sets.newHashSet(new User(userId), new User(userId2)));
			}else{
				return new DeletedConversation(Sets.newHashSet(new User(userId), new User(userId2)), userId2);
			}
		} catch (Exception e) {
			return null;
		}
	}

	private ConversationTextMessage toMessage(String conversationId, MigrationMessage oldMessage) {
		try {
			return new ConversationTextMessage(new User(oldMessage.getSender()), conversationId,
											   new Date(oldMessage.getDate() * 1000), oldMessage.getText(), "CRACK_ME",
											   false);
		} catch (Exception e) {
			return null;
		}
	}

	public String toApplication(MigrationApplication oldApplication) {
		return oldApplication.name();
	}

	public Set<String> mapApplications(Set<MigrationApplication> applications) {
		return applications.stream().map(this::toApplication).collect(Collectors.toSet());
	}

	public List<ConversationMessage> mapMessages(Conversation conversation, List<MigrationMessage> messages) {
		return messages.stream()
					   .map(message -> toMessage(conversation.getId(), message))
					   .filter(message -> message != null)
					   .collect(Collectors.toList());
	}

}
