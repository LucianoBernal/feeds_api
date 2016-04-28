package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.error.GetReceiptsException;
import com.etermax.conversations.model.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ElasticSearchDataMapperVisitor {

	public ConversationMessage visit(ElasticSearchTextMessage elasticSearchTextMessage) {
		try {

			String conversationId = elasticSearchTextMessage.getConversationId();
			String application = elasticSearchTextMessage.getApplication();
			Boolean ignored = ! elasticSearchTextMessage.getIgnoredBy().isEmpty();
			ConversationTextMessage conversationTextMessage = new ConversationTextMessage(
					new User(elasticSearchTextMessage.getSender()), conversationId,
					new Date(elasticSearchTextMessage.getDate()), elasticSearchTextMessage.getText(),
					application, ignored);
			String messageId = elasticSearchTextMessage.getId();
			conversationTextMessage.setId(messageId);
			List<IndividualMessageReceipt> messageReceipts = getMessageReceipts(elasticSearchTextMessage);
			MessageReceipt messageReceipt = null;
			if(!messageReceipts.isEmpty()) {
				messageReceipt = new MessageReceipt(messageId, messageReceipts, conversationId, application);
			}
			conversationTextMessage.addMessageReceipt(messageReceipt);
			return conversationTextMessage;
		} catch (Exception e) {
			return null;
		}
	}

	public Event visit(ElasticSearchEvent elasticSearchEvent) {
		try {
			Event event = new Event(elasticSearchEvent.getKey(), elasticSearchEvent.getEventsData(),
					elasticSearchEvent.getConversationId(), elasticSearchEvent.getUserId(),
					new Date(elasticSearchEvent.getDate()), elasticSearchEvent.getApplication());
			event.setId(elasticSearchEvent.getId());
			return event;
		} catch (Exception e) {
			return null;
		}
	}

	private List<IndividualMessageReceipt> getMessageReceipts(ElasticSearchMessage elasticSearchMessage)
			throws GetReceiptsException {
		return elasticSearchMessage.getReceipts().stream().map(this::fromElasticIndividualReceipt)
				.collect(Collectors.toList());

	}

	private IndividualMessageReceipt fromElasticIndividualReceipt(
			ElasticSearchIndividualMessageReceipt elasticIndividualReceipt) {
		String typeName = elasticIndividualReceipt.getType();
		ReceiptType type;
		if (typeName.equals("read")) {
			type = new ReadType();
		} else {
			type = new ReceivedType();
		}
		Long user = elasticIndividualReceipt.getUserId();
		return new IndividualMessageReceipt(type, user);
	}

	public MessageReceipt visit(ElasticsearchMessageReceipt elasticsearchMessageReceipt) {
		String id = elasticsearchMessageReceipt.getId();
		List<IndividualMessageReceipt> receipts = elasticsearchMessageReceipt.getReceipts().stream()
				.map(this::fromElasticIndividualReceipt).collect(Collectors.toList());
		String conversationId = elasticsearchMessageReceipt.getConversationId();
		String application = elasticsearchMessageReceipt.getApplication();
		return new MessageReceipt(id, receipts, conversationId, application);
	}
}
