package com.etermax.conversations.retrocompatibility.migration.service;

import com.etermax.conversations.model.*;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationResult;
import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MigrationService {

	private MigrationRepository oldRepository;
	private ConversationRepository newRepository;

	public MigrationService(MigrationRepository oldRepository, ConversationRepository newRepository) {
		this.oldRepository = oldRepository;
		this.newRepository = newRepository;
	}

	public List<MigrationResult> migrateConversations(Long userId) {
		if (!hasUserAlreadyBeenMigrated(userId)) {
			return migrateConversations(getConversationsToMigrate(userId));
		} else {
			return new ArrayList<>();
		}
	}

	private List<ConversationMessage> createAppMessages(Set<String> applications, ConversationTextMessage message,
			Conversation conversation) {
		return applications.stream()
						   .map(application -> createAppMessage(message, application, conversation))
						   .collect(Collectors.toList());
	}

	private ConversationMessage createAppMessage(ConversationTextMessage msg, String app, Conversation conversation) {
		return new ConversationTextMessage(msg.getSender(), msg.getConversationId(), msg.getDate(), msg.getText(), app,
										   msg.getIgnored());
	}

	private Long getReceiver(ConversationTextMessage msg, Conversation conversation) {
		return conversation.getUserIds().stream().filter(id -> !id.equals(msg.getSender().getId())).findFirst().get();
	}

	private ConversationMessage addReceipt(ConversationTextMessage message, Long receiverId) {
		List<IndividualMessageReceipt> receipts = Arrays.asList(
				new IndividualMessageReceipt(new ReceivedType(), receiverId),
				new IndividualMessageReceipt(new ReadType(), receiverId));
		MessageReceipt messageReceipt = new MessageReceipt(message.getId(), receipts, message.getConversationId(),
														   message.getApplication());
		message.addMessageReceipt(messageReceipt);
		return message;
	}

	private Map<Long, Set<String>> getApplications(Set<Long> conversationUsers) {
		return oldRepository.getApplications(conversationUsers);
	}

	private Set<Long> getUserIdsFromConversations(List<Conversation> conversations) {
		return conversations.stream()
							.map(Conversation::getUsers)
							.flatMap(Collection::stream)
							.map(User::getId)
							.collect(Collectors.toSet());
	}

	private Boolean hasUserAlreadyBeenMigrated(Long userId) {
		return oldRepository.checkAndSetMigration(userId);
	}

	private MigrationResult createMigrationResult(Map<String, List<ConversationMessage>> messages,
			Conversation conversation) {
		if (messages.get(conversation.getId()) == null) {
			return new MigrationResult(conversation, new ArrayList<>(), false);
		}
		return new MigrationResult(conversation, messages.get(conversation.getId()), true);
	}

	private List<Conversation> getConversationsToMigrate(Long userId) {
		List<Conversation> oldConversations = oldRepository.getConversations(userId);
		oldConversations.forEach(conversation -> conversation.setId(newRepository.createConversationId(conversation)));
		List<Conversation> userConversations = newRepository.getUserConversations(userId);
		return filterConversations(oldConversations, userConversations);
	}

	public Map<Long, Set<String>> getApplicationsFromUsers(Set<Long> userIds) {
		return getApplications(userIds);
	}

	private Set<String> getApplicationsInCommonForConversation(Conversation conversation,
			Map<Long, Set<String>> applications) {
		return conversation.getUserIds().stream().map(applications::get).reduce(Sets::intersection).get();
	}

	private Collector<ConversationMessage, ?, Map<String, List<ConversationMessage>>> groupByConversation() {
		return Collectors.groupingBy(ConversationMessage::getConversationId);
	}

	private List<Conversation> filterConversations(List<Conversation> oldConversations,
			List<Conversation> userConversations) {
		return oldConversations.stream().filter(oldConversation -> {
			return !userConversations.stream()
									 .filter(newConversation -> newConversation.getUserIds()
																			   .containsAll(
																					   oldConversation.getUserIds()))
									 .findAny()
									 .isPresent();
		}).collect(Collectors.toList());
	}

	private List<MigrationResult> migrateConversations(List<Conversation> conversations) {
		return migrateConversationsAndMessages(mergeConversationsWithMessages(conversations));
	}

	private Map<Conversation, List<ConversationMessage>> mergeConversationsWithMessages(
			List<Conversation> conversations) {
		return filterConversationsWithMessages(conversations, getConversationMessages(conversations));
	}

	private Map<Conversation, List<ConversationMessage>> getConversationMessages(List<Conversation> conversations) {
		Map<Long, Set<String>> applications = getApplicationsFromUsers(getUserIdsFromConversations(conversations));
		Map<Conversation, List<ConversationMessage>> messages = oldRepository.getMessages(conversations);
		return getApplicationsMessages(messages, applications);
	}

	public Map<Conversation, List<ConversationMessage>> getApplicationsMessages(
			Map<Conversation, List<ConversationMessage>> messagesByConversation, Map<Long, Set<String>> applications) {

		Map<Conversation, List<ConversationMessage>> response = new HashMap<>();
		messagesByConversation.entrySet().forEach(entry -> {
			response.put(entry.getKey(), getApplicationMessages(entry.getKey(), entry.getValue(), applications));
		});

		return response;
	}

	private List<ConversationMessage> getApplicationMessages(Conversation conversation,
			List<ConversationMessage> messages, Map<Long, Set<String>> applications) {

		Set<String> conversationApplications = getApplicationsInCommonForConversation(conversation, applications);

		return messages.stream()
					   .map(message -> (ConversationTextMessage) message)
					   .flatMap(message -> createAppMessages(conversationApplications, message, conversation).stream())
					   .collect(Collectors.toList());
	}

	private List<MigrationResult> migrateConversationsAndMessages(Map<Conversation, List<ConversationMessage>> map) {
		List<Conversation> conversations = new ArrayList<>(map.keySet());
		List<ConversationMessage> messages = map.values()
												.stream()
												.flatMap(Collection::stream)
												.collect(Collectors.toList());

		if (conversations.isEmpty()) {
			return new ArrayList<>();
		}

		newRepository.saveConversations(conversations);
		return migrateConversationMessages(conversations, messages).stream()
																   .filter(MigrationResult::hasBeenMigrated)
																   .collect(Collectors.toList());
	}

	private Map<Conversation, List<ConversationMessage>> filterConversationsWithMessages(
			List<Conversation> conversations, Map<Conversation, List<ConversationMessage>> messages) {

		Map<Conversation, List<ConversationMessage>> result = new HashMap<>();

		conversations.stream().forEach(conversation -> {
			List<ConversationMessage> conversationMessages = messages.get(conversation);
			if (conversationMessages != null && !conversationMessages.isEmpty()) {
				result.put(conversation, conversationMessages);
			}
		});

		return result;
	}

	private List<MigrationResult> migrateConversationMessages(List<Conversation> conversations,
			List<ConversationMessage> messages) {

		if (messages.isEmpty()) {
			return new ArrayList<>();
		}

		List<ConversationMessage> migrated = newRepository.saveMessages(messages);
		Map<String, List<ConversationMessage>> migratedByConversation = migrated.stream()
																				.collect(groupByConversation());

		return conversations.stream()
							.map(conversation -> createMigrationResult(migratedByConversation, conversation))
							.collect(Collectors.toList());
	}

}
