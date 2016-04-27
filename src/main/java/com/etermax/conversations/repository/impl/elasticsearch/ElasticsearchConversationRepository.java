package com.etermax.conversations.repository.impl.elasticsearch;

import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.error.MessageNotFoundException;
import com.etermax.conversations.error.UserNotInConversationException;
import com.etermax.conversations.factory.AddressedMessageFactory;
import com.etermax.conversations.model.*;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.repository.impl.elasticsearch.dao.ElasticsearchDAO;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.CounterDAO;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticSearchIndividualMessageReceipt;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticSearchMessage;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticsearchConversation;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticsearchDataList;
import com.etermax.conversations.repository.impl.elasticsearch.mapper.ElasticSearchModelMapper;
import com.etermax.conversations.repository.impl.elasticsearch.strategy.ConversationIdGenerationStrategy;
import com.google.common.collect.Sets;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class ElasticsearchConversationRepository implements ConversationRepository {

	private ConversationIdGenerationStrategy idGenerator;
	private ElasticsearchDAO dao;
	private CounterDAO unreadMessagesDAO;
	private ElasticSearchModelMapper mapper;

	public ElasticsearchConversationRepository(Client client, Integer maxMessages,
			AddressedMessageFactory addressedMessageFactory, ConversationIdGenerationStrategy idGenerator,
			CounterDAO unreadMessagesDAO) {

		this.unreadMessagesDAO = unreadMessagesDAO;
		this.dao = new ElasticsearchDAO(client, "crack", maxMessages);
		this.mapper = new ElasticSearchModelMapper(addressedMessageFactory);
		this.idGenerator = idGenerator;
	}

	@Override
	public Conversation saveConversation(Conversation conversation) {
		conversation.setId(createConversationId(conversation));
		dao.saveConversation(mapper.toElasticsearchConversation(conversation));
		return conversation;
	}

	@Override
	public List<Conversation> saveConversations(List<Conversation> conversations) {
		conversations.stream().forEach(conversation -> conversation.setId(createConversationId(conversation)));
		List<ElasticsearchConversation> conversationsToSave = conversations.stream()
																		   .map(mapper::toElasticsearchConversation)
																		   .collect(Collectors.toList());
		return dao.saveConversations(conversationsToSave)
				  .stream()
				  .map(mapper::fromElasticSearchConversation)
				  .collect(Collectors.toList());
	}

	@Override
	public ConversationMessage saveMessage(ConversationMessage conversationMessage, Conversation conversation)
			throws UserNotInConversationException, ConversationNotFoundException {
		String conversationId = conversation.getId();
		conversationMessage.setId(dao.createConversationDataId(conversationId, conversationMessage.getDate()));
		dao.saveMessage(mapper.toElasticsearchMessage(conversationMessage, conversation));

		updateLastActivity(conversation);
		incrementUnreadMessages(conversationMessage, conversation);

		return conversationMessage;
	}

	private void updateLastActivity(Conversation conversation) {
		dao.updateLastActivity(conversation.getId());
	}

	@Override
	public void saveEvent(Event event) {
		event.setId(dao.createConversationDataId(event.getConversationId(), event.getDate()));
		dao.saveEvent(mapper.toElasticsearchEvent(event));
	}

	@Override
	public Conversation getConversationWithId(String conversationId) throws ConversationNotFoundException {
		return mapper.fromElasticSearchConversation(dao.getConversationById(conversationId));
	}

	private List<Conversation> getConversationsById(List<String> conversationIds) throws
			ConversationNotFoundException {
		List<ElasticsearchConversation> conversations = dao.getConversationsById(conversationIds);
		return conversations.stream().map(mapper::fromElasticSearchConversation).collect(Collectors.toList());
	}

	@Override
	public Conversation getConversationWithUsers(Set<Long> userIds) throws ConversationNotFoundException {
		ElasticsearchConversation conversation = dao.getConversationWithUsers(userIds);
		if (conversation == null) {
			throw new ConversationNotFoundException();
		}
		return mapper.fromElasticSearchConversation(conversation);
	}

	@Override
	public List<Conversation> getUserConversations(Long userId) {
		return dao.getUserConversations(userId)
				  .stream()
				  .map(mapper::fromElasticSearchConversation)
				  .collect(Collectors.toList());
	}

	@Override
	public List<Conversation> getUserActiveConversations(Long userId, String application) {
		return dao.getUserConversations(userId)
				  .stream()
				  .filter(elasticConversation -> elasticConversation.hasDeleted(userId + application))
				  .map(mapper::fromElasticSearchConversation)
				  .collect(Collectors.toList());
	}

	@Override
	public List<AddressedMessage> getAddressedMessages(List<Long> userIds, Date date, String application) {
		ElasticsearchConversation conversation = dao.getConversationWithUsers(Sets.newHashSet(userIds));
		List<ElasticSearchMessage> addressedMessages = dao.getAddressedMessages(conversation.getId(), userIds.get(0),
																				date, application);
		return mapper.buildAddressedMessages(conversation, addressedMessages);
	}

	@Override
	public void deleteMessage(String conversationId, String messageId, Long userId) throws MessageNotFoundException {
		dao.deleteMessage(conversationId, messageId, userId);
		dao.updateLastActivity(conversationId);
	}

	@Override
	public ConversationHistory getConversationHistory(String conversationId, Range range, Long userId,
			String application) throws ConversationNotFoundException, UserNotInConversationException {
		ElasticsearchDataList dataInRange = dao.getConversationDataInRange(conversationId, range, userId, application);
		return mapper.buildConversationHistoryFromResult(dataInRange, range.getFirstDate());
	}

	@Override
	public void deleteConversation(String conversationId, Long userId, Date deletionDate, String app) {
		dao.deleteConversation(conversationId, userId, deletionDate.getTime(), app);
		dao.updateLastActivity(conversationId);
	}

	@Override
	public ConversationMessage getConversationMessage(String conversationId, String messageId, Long userId) {
		ElasticSearchMessage elasticSearchMessage = dao.getConversationMessage(conversationId, messageId, userId);
		return (ConversationMessage) mapper.fromElasticsearchConversationData(elasticSearchMessage);
	}

	@Override
	public void resetRead(String conversationId, String app, Long userId) {
		unreadMessagesDAO.resetUnreadMessages(userId, conversationId, app);
	}

	@Override
	public IndividualMessageReceipt saveReceiptInMessage(String conversationId, String messageId,
			IndividualMessageReceipt receipt) {
		ElasticSearchIndividualMessageReceipt elasticsearchIndividualReceipt = mapper
				.buildElasticsearchIndividualMessageReceipt(receipt);

		elasticsearchIndividualReceipt = dao.saveReceiptInMessage(elasticsearchIndividualReceipt, conversationId,
																  messageId);
		String app = dao.getConversationMessage(conversationId, messageId, receipt.getUser()).getApplication();
		ReadResetter readResetter = new ReadResetter(conversationId, app, receipt.getUser(), this);
		receipt.getType().accept(readResetter);

		return mapper.toIndividualMessageReceipt(elasticsearchIndividualReceipt);
	}

	@Override
	public List<ConversationSync> getConversationSyncData(Long userId, List<String> conversationIds, Date date,
			String application) {
		List<ElasticsearchDataList> data = dao.getConversationSyncData(userId, conversationIds, date, application);
		Map<String, Long> unreadMessages = unreadMessagesDAO.getUnreadMessages(userId, conversationIds, application);
		return mapper.buildConversationSyncFromResult(data, unreadMessages, date);
	}

	@Override
	public List<ConversationMessage> saveMessages(List<ConversationMessage> messages) {

		Map<String, List<Conversation>> conversations = getConversations(messages);

		List<ConversationMessage> conversationMessages = messages.stream().map(message -> {
			String conversationId = message.getConversationId();
			message.setId(dao.createConversationDataId(conversationId, message.getDate()));
			return message;
		}).collect(Collectors.toList());

		Map<String, List<ElasticSearchMessage>> elasticSearchMessages = conversationMessages.stream().map(message -> {
			Conversation conversation = conversations.get(message.getConversationId()).get(0);
			return mapper.toElasticsearchMessage(message, conversation);
		}).collect(Collectors.groupingBy(ElasticSearchMessage::getConversationId));

		dao.saveMessages(elasticSearchMessages);

		return conversationMessages;

	}

	/*TODO poli: El limite de mensajes no debería manejarse aca. Habría que filtrar la lista de conversaciones en
	  el método de chatHeaders, pero para eso habria que crear un retrocompatibilityService y dejar de hacer toda
	  la logica en el adapter (por ahora esta comentado)*/
	@Override
	public Map<String, AddressedMessage> getLastMessages(Long userId, List<String> conversationIds, String application)
			throws ConversationNotFoundException {
		Map<String, AddressedMessage> response = new HashMap<>();
		List<Conversation> conversations = getConversationsById(conversationIds);
		List<ElasticsearchConversation> activeConversations = dao.getActiveUserConversations(conversations, userId,
																							 application);

		activeConversations = activeConversations.stream()
												 .sorted((o1, o2) -> o2.getLastActivity()
																	   .compareTo(o1.getLastActivity()))
												 .limit(250)
												 .collect(Collectors.toList());

		List<Conversation> activeUserConversations = activeConversations.stream()
																		.map(mapper::fromElasticSearchConversation)
																		.collect(Collectors.toList());
		Map<Conversation, Optional<ElasticSearchMessage>> lastMessagesOptional = dao.getLastMessages(
				activeUserConversations, application, userId);

		lastMessagesOptional.entrySet()
							.stream()
							.forEach(conversationOptionLastMessage -> response.put(
									conversationOptionLastMessage.getKey().getId(),
									mapper.toAddressedMessage((conversationOptionLastMessage.getValue().get()),
															  conversationOptionLastMessage.getKey())));

		return response;
	}

	@Override
	public boolean isAlreadyAcknowledged(String conversationId, String messageId, IndividualMessageReceipt receipt) {
		return dao.isAlreadyAcknowledged(conversationId, messageId,
										 mapper.buildElasticsearchIndividualMessageReceipt(receipt));
	}

	@Override
	public String getMessageApplication(String conversationId, String messageId) {
		return dao.getMessageApplication(conversationId, messageId);
	}

	@Override
	public Map<String, Long> getUnreadMessagesCount(List<String> conversations, Long userId, String application) {
		return unreadMessagesDAO.getUnreadMessages(userId, conversations, application);
	}

	private AddressedMessage getLastMessage(String conversationId, String app) throws ConversationNotFoundException {
		Conversation conversation = getConversationWithId(conversationId);
		Optional<ElasticSearchMessage> lastMessageOptional = dao.getLastMessage(conversationId, app);
		return lastMessageOptional.map(lastMessage -> mapper.toAddressedMessage(lastMessage, conversation))
								  .orElse(NullAddressedMessage.create(conversation));
	}

	@Override
	public void clearRepository() {
		dao.clearRepository();
	}

	public void flush() {
		dao.flush();
	}

	private Map<String, List<Conversation>> getConversations(List<ConversationMessage> messages) {
		Set<String> conversationIds = messages.stream()
											  .collect(Collectors.groupingBy(ConversationMessage::getConversationId))
											  .keySet();

		return conversationIds.stream()
							  .map(this::getConversationWithId)
							  .collect(Collectors.groupingBy(Conversation::getId));
	}

	@Override
	public String createConversationId(Conversation conversation) {
		if (conversation.getId() == null) {
			return idGenerator.generateId(conversation);
		} else {
			return conversation.getId();
		}
	}

	private void incrementUnreadMessages(ConversationMessage conversationMessage, Conversation conversation) {
		Set<Long> userIds = conversation.getUserIds();
		userIds.remove(conversationMessage.getSender().getId());
		userIds.forEach(user -> unreadMessagesDAO.incrementUnreadMessages(user, conversation.getId(),
																		  conversationMessage.getApplication()));
	}

}
