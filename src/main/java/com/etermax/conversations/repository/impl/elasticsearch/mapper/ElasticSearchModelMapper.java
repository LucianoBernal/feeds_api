package com.etermax.conversations.repository.impl.elasticsearch.mapper;

import com.etermax.conversations.error.InvalidConversation;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.model.*;
import com.etermax.conversations.repository.impl.elasticsearch.domain.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ElasticSearchModelMapper {
	public ElasticSearchModelMapper() {
	}

	public ElasticsearchConversation toElasticsearchConversation(Conversation conversation) {
			return new ElasticsearchConversation(conversation.getId(), conversation.getUserIds(), "SINGLE");
	}

	public ElasticSearchMessage toElasticsearchMessage(ConversationMessage message, Conversation conversation) {
		ConversationMessageMapperVisitor conversationMessageMapperVisitor = new ConversationMessageMapperVisitor(
				conversation);
		ElasticSearchMessage elasticSearchMessage = message.accept(conversationMessageMapperVisitor);
		elasticSearchMessage.setConversationId(conversation.getId());
		return elasticSearchMessage;
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

}
