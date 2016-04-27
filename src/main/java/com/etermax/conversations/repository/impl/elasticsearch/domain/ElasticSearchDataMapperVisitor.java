package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.error.GetReceiptsException;
import com.etermax.conversations.model.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ElasticSearchDataMapperVisitor {
	public ConversationMessage visit(ElasticSearchAudioMessage elasticSearchAudioMessage) {
		try {
			String conversationId = elasticSearchAudioMessage.getConversationId();
			String application = elasticSearchAudioMessage.getApplication();
			Boolean ignored = ! elasticSearchAudioMessage.getIgnoredBy().isEmpty();
			ConversationAudioMessage conversationAudioMessage = new ConversationAudioMessage(
					new User(elasticSearchAudioMessage.getSender()), conversationId,
					new Date(elasticSearchAudioMessage.getDate()), elasticSearchAudioMessage.getUrl(),
					elasticSearchAudioMessage.getLength(), elasticSearchAudioMessage.getFormat(), application, ignored);
			String messageId = elasticSearchAudioMessage.getId();
			conversationAudioMessage.setId(messageId);
			MessageReceipt messageReceipt = null;
			List<IndividualMessageReceipt> messageReceipts = getMessageReceipts(elasticSearchAudioMessage);
			if(!messageReceipts.isEmpty()) {
				messageReceipt = new MessageReceipt(messageId, messageReceipts, conversationId, application);
			}
			conversationAudioMessage.addMessageReceipt(messageReceipt);
			return conversationAudioMessage;
		} catch (Exception e) {
			return null;
		}
	}

	public ConversationMessage visit(ElasticSearchImageMessage elasticSearchImageMessage) {
		try {
			String conversationId = elasticSearchImageMessage.getConversationId();
			String application = elasticSearchImageMessage.getApplication();
			Boolean ignored = ! elasticSearchImageMessage.getIgnoredBy().isEmpty();
			ConversationImageMessage conversationImageMessage = new ConversationImageMessage(
					new User(elasticSearchImageMessage.getSender()), conversationId,
					new Date(elasticSearchImageMessage.getDate()), elasticSearchImageMessage.getUrl(),
					elasticSearchImageMessage.getThumbnail(), elasticSearchImageMessage.getFormat(),
					elasticSearchImageMessage.getOrientation(), application, ignored);
			String messageId = elasticSearchImageMessage.getId();
			conversationImageMessage.setId(messageId);
			MessageReceipt messageReceipt = null;
			List<IndividualMessageReceipt> messageReceipts = getMessageReceipts(elasticSearchImageMessage);
			if(!messageReceipts.isEmpty()) {
				messageReceipt = new MessageReceipt(messageId, messageReceipts, conversationId, application);
			}
			conversationImageMessage.addMessageReceipt(messageReceipt);
			return conversationImageMessage;
		} catch (Exception e) {
			return null;
		}
	}

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

	public ConversationMessage visit(ElasticSearchVideoMessage elasticSearchVideoMessage) {
		try {
			String conversationId = elasticSearchVideoMessage.getConversationId();
			String application = elasticSearchVideoMessage.getApplication();
			Boolean ignored = ! elasticSearchVideoMessage.getIgnoredBy().isEmpty();
			ConversationVideoMessage conversationVideoMessage = new ConversationVideoMessage(
					new User(elasticSearchVideoMessage.getSender()), conversationId,
					new Date(elasticSearchVideoMessage.getDate()), elasticSearchVideoMessage.getUrl(),
					elasticSearchVideoMessage.getThumbnail(), elasticSearchVideoMessage.getLength(),
					elasticSearchVideoMessage.getFormat(), elasticSearchVideoMessage.getOrientation(),
					application, ignored);
			String messageId = elasticSearchVideoMessage.getId();
			conversationVideoMessage.setId(messageId);
			MessageReceipt messageReceipt = null;
			List<IndividualMessageReceipt> messageReceipts = getMessageReceipts(elasticSearchVideoMessage);
			if(!messageReceipts.isEmpty()) {
				messageReceipt = new MessageReceipt(messageId, messageReceipts, conversationId, application);
			}
			conversationVideoMessage.addMessageReceipt(messageReceipt);
			return conversationVideoMessage;
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
