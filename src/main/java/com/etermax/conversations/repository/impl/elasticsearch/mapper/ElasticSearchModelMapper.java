package com.etermax.conversations.repository.impl.elasticsearch.mapper;

import com.etermax.conversations.error.InvalidConversation;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.factory.AddressedMessageFactory;
import com.etermax.conversations.model.*;
import com.etermax.conversations.repository.impl.elasticsearch.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class ElasticSearchModelMapper {
	private AddressedMessageFactory addressedMessageFactory;

	public ElasticSearchModelMapper(AddressedMessageFactory addressedMessageFactory) {
		this.addressedMessageFactory = addressedMessageFactory;
	}

	public ElasticsearchConversation toElasticsearchConversation(Conversation conversation) {
		//TODO: Borrar luego de migracion
		if (conversation instanceof DeletedConversation) {
			ElasticsearchConversation eConv = new ElasticsearchConversation(conversation.getId(),
																			conversation.getUserIds(), "SINGLE");
			eConv.setDeletedBy(createDeletionData((DeletedConversation) conversation));
			return eConv;
		} else {
			return new ElasticsearchConversation(conversation.getId(), conversation.getUserIds(), "SINGLE");
		}
	}

	//TODO: Borrar luego de migracion
	private List<DeletionData> createDeletionData(DeletedConversation deletedConversation) {
		List<String> apps = Arrays.asList("ANGRY_WORDS", "ANGRY_MIX", "TRIVIA_CRACK", "CHANNELS", "TRIVIA_CRACK_FCB",
										  "TRIVIA_CRACK_FCM");

		return apps.stream()
				   .map(app -> new DeletionData(deletedConversation.getDeletedBy() + "-" + app, new Date().getTime()))
				   .collect(Collectors.toList());
	}

	public ElasticSearchMessage toElasticsearchMessage(ConversationMessage message, Conversation conversation) {
		ConversationMessageMapperVisitor conversationMessageMapperVisitor = new ConversationMessageMapperVisitor(
				conversation);
		ElasticSearchMessage elasticSearchMessage = message.accept(conversationMessageMapperVisitor);
		elasticSearchMessage.setConversationId(conversation.getId());
		return elasticSearchMessage;
	}

	//TODO: BORRAR DESPUES DE LA MIGRACION
	private void addReceiptsToMessage(ConversationMessage message, ElasticSearchMessage elasticSearchMessage) {
		if (message.getMessageReceipt() != null && message.getMessageReceipt().getReceipts() != null
				&& !message.getMessageReceipt().getReceipts().isEmpty()) {
			elasticSearchMessage.addReceipts(buildReceiptsFromMessage(message));
		}
	}

	private List<ElasticSearchIndividualMessageReceipt> buildReceiptsFromMessage(ConversationMessage message) {
		return message.getMessageReceipt()
					  .getReceipts()
					  .stream()
					  .map(this::buildElasticsearchIndividualMessageReceipt)
					  .collect(Collectors.toList());
	}

	public Conversation fromElasticSearchConversation(ElasticsearchConversation conversation) {
		Set<User> users = conversation.getUsers().stream().map(userId -> {
			try {
				return new User(userId.longValue());
			} catch (InvalidUserException e) {
				return null;
			}
		}).collect(Collectors.toSet());

		try {
			Conversation result = new Conversation(users);
			result.setId(conversation.getId());
			return result;
		} catch (InvalidConversation invalidConversation) {
			return null;
		}
	}

	public ConversationData fromElasticsearchConversationData(ElasticSearchConversationData data) {
		try {
			ElasticSearchDataMapperVisitor elasticSearchDataMapperVisitor = new ElasticSearchDataMapperVisitor();
			return data.accept(elasticSearchDataMapperVisitor);
		} catch (Exception e) {
			return null;
		}
	}

	public List<ConversationSync> buildConversationSyncFromResult(List<ElasticsearchDataList> dataInRange,
			Map<String, Long> unreadMessages, Date date) {
		return dataInRange.stream().map(range -> {
			List<ConversationData> data = range.getList()
											   .stream()
											   .map(this::fromElasticsearchConversationData)
											   .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
											   .collect(Collectors.toList());
			Long hasMoreFirstDate = date.getTime();
			List<ElasticSearchConversationData> dataList = range.getList();
			Long hasMoreLastDate = dataList.get(dataList.size() - 1).getDate() - 1;
			HasMore hasMore = new HasMore(range.getTotal().intValue(), new Date(hasMoreFirstDate),
										  new Date(hasMoreLastDate));
			return new ConversationSync(range.getConversationId(), data, hasMore,
										unreadMessages.get(range.getConversationId()));
		}).collect(Collectors.toList());
	}

	public ConversationHistory buildConversationHistoryFromResult(ElasticsearchDataList dataRange, Long firstDate) {
		List<ConversationData> data = dataRange.getList()
											   .stream()
											   .map(this::fromElasticsearchConversationData)
											   .collect(Collectors.toList());
		Long actualFirstDate = firstDate == null ? 0 : firstDate;
		Integer dataLeft = dataRange.getTotal().intValue();
		HasMore hasMore;
		if (!dataLeft.equals(0)) {
			Long hasMoreLastDate = data.get(data.size() - 1).getDate().getTime();
			hasMore = new HasMore(dataLeft, new Date(actualFirstDate), new Date(hasMoreLastDate));
		} else {
			hasMore = new HasMore(dataLeft, null, null);
		}
		return new ConversationHistory(data, hasMore);
	}

	public ElasticSearchEvent toElasticsearchEvent(Event event) {
		return new ElasticSearchEvent(event.getConversationId(), event.getId(), event.getDate(), event.getKey(),
									  event.getEventsData(), event.getUserId(), event.getApplication());
	}

	public ElasticSearchIndividualMessageReceipt buildElasticsearchIndividualMessageReceipt(
			IndividualMessageReceipt receipt) {
		return new ElasticSearchIndividualMessageReceipt(receipt.getType().toString(), receipt.getUser(),
														 receipt.getDate().getTime());
	}

	public IndividualMessageReceipt toIndividualMessageReceipt(
			ElasticSearchIndividualMessageReceipt elasticsearchIndividualReceipt) {
		ReceiptType type;
		if (elasticsearchIndividualReceipt.getType().equals("read")) {
			type = new ReadType();
		} else {
			type = new ReceivedType();
		}
		Long userId = elasticsearchIndividualReceipt.getUserId();
		return new IndividualMessageReceipt(type, userId);
	}

	public List<AddressedMessage> buildAddressedMessages(ElasticsearchConversation elasticsearchConversation,
			List<ElasticSearchMessage> addressedMessages) {
		Conversation conversation = fromElasticSearchConversation(elasticsearchConversation);
		return addressedMessages.stream()
								.map(elasticSearchMessage -> toAddressedMessage(elasticSearchMessage, conversation))
								.collect(Collectors.toList());
	}

	public AddressedMessage toAddressedMessage(ElasticSearchMessage elasticSearchMessage, Conversation conversation) {
		ConversationMessage message = (ConversationMessage) elasticSearchMessage.accept(
				new ElasticSearchDataMapperVisitor());
		return addressedMessageFactory.createAddressedMessage(message, conversation);
	}

}
