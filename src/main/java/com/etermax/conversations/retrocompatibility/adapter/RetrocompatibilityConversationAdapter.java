package com.etermax.conversations.retrocompatibility.adapter;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.DeleteConversationException;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.factory.AddressedMessageFactory;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.notification.service.NotificationService;
import com.etermax.conversations.retrocompatibility.dto.*;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationResult;
import com.etermax.conversations.retrocompatibility.migration.service.MigrationService;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityConversationService;
import com.etermax.conversations.service.ConversationService;
import com.google.common.collect.Sets;
import dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrocompatibility.dto.RetrocompatibilityUserDTO;
import rx.Observable;

import javax.ws.rs.GET;
import java.util.*;
import java.util.stream.Collectors;

public class RetrocompatibilityConversationAdapter {

	private MigrationService migrationService;
	private ConversationAdapter conversationAdapter;
	private MessageAdapter messageAdapter;
	private RetrocompatibilityUserAdapter userAdapter;
	private AddressedMessageFactory factory;
	private ConversationService conversationService;
	private RetrocompatibilityConversationService retrocompatibilityConversationService;
	private static final Logger logger = LoggerFactory.getLogger(RetrocompatibilityConversationAdapter.class);

	public RetrocompatibilityConversationAdapter(MigrationService migrationService,
			ConversationAdapter conversationAdapter, MessageAdapter messageAdapter,
			RetrocompatibilityUserAdapter userAdapter, ConversationService conversationService,
			RetrocompatibilityConversationService retrocompatibilityConversationService) {
		this.migrationService = migrationService;
		this.conversationAdapter = conversationAdapter;
		this.messageAdapter = messageAdapter;
		this.userAdapter = userAdapter;
		this.conversationService = conversationService;
		this.retrocompatibilityConversationService = retrocompatibilityConversationService;
	}

	public RetrocompatibilityConversationDTO getMessages(List<Long> userIds, String application) {
		// MIGRATION
		List<AddressedMessageDisplayDTO> messages = migrateConvrsationsAndGet(userIds, application);
		if (messages == null) {
			messages = messageAdapter.getRetrocompatibilityUserMessages(userIds, null, application);
		}
		UserDTO userDTO = userAdapter.getUser(userIds);
		return new RetrocompatibilityConversationDTO(toDTO(messages), userDTO);
	}

	private List<AddressedMessageDisplayDTO> migrateConvrsationsAndGet(List<Long> userIds, String application) {
		List<MigrationResult> migrationResults = migrationService.migrateConversations(userIds.get(0));
		List<AddressedMessageDisplayDTO> response = null;
		if (!migrationResults.isEmpty()) {
			response = migrationResults.stream()
									   .filter(result -> result.getConversation().getUserIds().containsAll(userIds))
									   .map(result -> toAddressedMessages(result.getConversation(),
																		  result.getMessagesMigrated(), application))
									   .findAny()
									   .orElse(null);
		}

		return response;
	}

	private List<AddressedMessageDisplayDTO> toAddressedMessages(Conversation conversation,
			List<ConversationMessage> messages, String application) {

		List<AddressedMessageDisplayDTO> response = messages.stream()
															.filter(message -> message.getApplication()
																					  .equals(application))
															.map(message -> new AddressedMessageFactory()
																	.createAddressedMessage(message, conversation))
															.map(AddressedMessageDisplayDTO::new)
															.sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
															.collect(Collectors.toList());

		return response.isEmpty() ? null : response;
	}

	public RetrocompatibilityMessageDTO saveMessage(AddressedMessageCreationDTO message) {
		migrationService.migrateConversations(message.getSenderId());
		message.setBlocked(getBlocked(message));
		RetrocompatibilityMessageDTO retrocompatibilityMessageDTO = new RetrocompatibilityMessageDTO(messageAdapter.saveMessage(message), 1);
		return retrocompatibilityMessageDTO;
	}

	@GET
	public RetrocompatibilityChatHeadersDTO getChatHeaders(Long userId, String application) {

		List<ConversationDisplayDTO> conversations = conversationAdapter.getUserConversations(userId);
		Map<String, AddressedMessageDisplayDTO> messages = messageAdapter.getLastMessages(userId, conversations,
																						  application);

		// MIGRATION
		List<MigrationResult> migrationResults = migrationService.migrateConversations(userId);
		conversations = mergeMigrationConversations(getMigratedConversations(migrationResults), conversations);
		messages = mergeMigrationMessages(getMigratedLastMessages(migrationResults, application), messages,
										  conversations, application);

		conversations = filterConversations(conversations, messages);
		Map<String, RetrocompatibilityUserDTO> receivers = getReceivers(userId, conversations);

		Map<String, Long> unreadMessages = retrocompatibilityConversationService.getUnreadMessagesCount(
				conversations.stream().map(ConversationDisplayDTO::getId).collect(Collectors.toList()), userId,
				application);
		List<RetrocompatibilityChatHeaderDTO> responseList = buildResponse(userId, conversations, messages, receivers,
																		   unreadMessages);

		return new RetrocompatibilityChatHeadersDTO(responseList);
	}

	public Map<String, AddressedMessageDisplayDTO> mergeMigrationMessages(
			List<AddressedMessageDisplayDTO> migratedMessages, Map<String, AddressedMessageDisplayDTO> messages,
			List<ConversationDisplayDTO> conversations, String application) {

		Map<String, AddressedMessageDisplayDTO> result = new HashMap<>();

		conversations.forEach(conversation -> {
			String conversationId = conversation.getId();
			if (messages.containsKey(conversationId)) {
				if (messages.get(conversationId).getApplication().equals(application)) {
					result.put(conversationId, messages.get(conversationId));
				}
			} else {
				migratedMessages.stream()
								.filter(message -> conversation.getUsers().contains(message.getSenderId())
										&& conversation.getUsers().contains(message.getReceiverId()))
								.filter(message -> message.getApplication().equals(application))
								.sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
								.findFirst()
								.ifPresent(message -> result.put(conversationId, message));
			}
		});

		return result;

	}

	public List<ConversationDisplayDTO> mergeMigrationConversations(List<ConversationDisplayDTO> migratedConversations,
			List<ConversationDisplayDTO> conversations) {
		conversations = Observable.from(conversations)
								  .mergeWith(Observable.from(migratedConversations))
								  .distinct(conversation -> conversation.getId())
								  .toList()
								  .toBlocking()
								  .single();
		return conversations;
	}

	public List<ConversationDisplayDTO> getMigratedConversations(List<MigrationResult> migrationResults) {
		if (!migrationResults.isEmpty()) {
			return migrationResults.stream().map(result -> {
				Conversation conversation = result.getConversation();
				return new ConversationDisplayDTO(conversation);
			}).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public List<AddressedMessageDisplayDTO> getMigratedLastMessages(List<MigrationResult> migrationResults,
			String application) {
		factory = new AddressedMessageFactory();
		List<AddressedMessageDisplayDTO> response = new ArrayList<>();
		if (!migrationResults.isEmpty()) {
			migrationResults.stream().forEach(result -> {
				Conversation conversation = result.getConversation();
				result.getMessagesMigrated()
					  .stream()
					  .map(message -> factory.createAddressedMessage(message, conversation))
					  .filter(addressedMessage -> addressedMessage.getApplication().equals(application))
					  .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
					  .reduce((t1, t2) -> t2)
					  .map(AddressedMessageDisplayDTO::new)
					  .ifPresent(message -> response.add(message));
			});
		}
		return response;
	}

	private List<RetrocompatibilityMessageDTO> toDTO(List<AddressedMessageDisplayDTO> messages) {
		List<RetrocompatibilityMessageDTO> dtos = new ArrayList<>();
		Integer counter = 1;
		for (AddressedMessageDisplayDTO message : messages) {
			dtos.add(new RetrocompatibilityMessageDTO(message, counter));
			counter += 1;
		}
		return dtos;
	}

	public List<RetrocompatibilityChatHeaderDTO> buildResponse(Long userId, List<ConversationDisplayDTO> conversations,
			Map<String, AddressedMessageDisplayDTO> messages, Map<String, RetrocompatibilityUserDTO> receivers,
			Map<String, Long> unreadMessages) {
		return conversations.stream().map(conversation -> {
			return new RetrocompatibilityChatHeaderDTO(userId, conversation, messages.get(conversation.getId()),
													   receivers.get(conversation.getId()),
													   unreadMessages.get(conversation.getId()));
		}).collect(Collectors.toList());
	}

	private Boolean getBlocked(AddressedMessageCreationDTO message) {
		UserDTO sender = userAdapter.getUser(Arrays.asList(message.getReceiverId(), message.getSenderId()));
		Map<String, Object> interactions = (Map<String, Object>) sender.getExtensions().get("social_interactions");
		return (Boolean) interactions.get("is_blocked");
	}

	private List<ConversationDisplayDTO> filterConversations(List<ConversationDisplayDTO> conversations,
			Map<String, AddressedMessageDisplayDTO> messages) {

		return conversations.stream()
							.filter(conversation -> messages.containsKey(conversation.getId()))
							.collect(Collectors.toList());
	}

	private Map<String, RetrocompatibilityUserDTO> getReceivers(Long userId,
			List<ConversationDisplayDTO> conversations) {

		List<RetrocompatibilityUserDTO> receivers = userAdapter.getReceivers(userId, conversations);
		Map<String, RetrocompatibilityUserDTO> result = new HashMap<>();

		conversations.forEach(conversation -> {
			Long otherUserId = conversation.getUsers()
										   .stream()
										   .filter(userId2 -> !userId2.equals(userId))
										   .findAny()
										   .get();
			RetrocompatibilityUserDTO otherUser = receivers.stream()
														   .filter(userDTO -> userDTO.getId().equals(otherUserId))
														   .findAny()
														   .get();
			result.put(conversation.getId(), otherUser);
		});

		return result;
	}

	public void deleteConversation(RetrocompatibilityConversationDeletionDTO deletionDTO) {
		try {
			deletionDTO.validate();
			Conversation conversationWithUsers = conversationService.getConversationWithUsers(
					Sets.newHashSet(deletionDTO.getFirstUserId(), deletionDTO.getSecondUserId()));
			String conversationId = conversationWithUsers.getId();
			String application = deletionDTO.getApplication();
			Date deletionDate = new Date();
			Long deletionUserId = deletionDTO.getFirstUserId();
			conversationService.deleteConversation(conversationId, deletionUserId, application, deletionDate);
		} catch (DeleteConversationException | InvalidDTOException e) {
			throw new ClientException(e, 400);
		}
	}

}
