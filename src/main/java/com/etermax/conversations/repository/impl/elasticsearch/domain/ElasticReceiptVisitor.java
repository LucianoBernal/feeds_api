package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.List;
import java.util.Set;

public class ElasticReceiptVisitor {
	private Long userId;

	public ElasticReceiptVisitor(Long userId) {
		this.userId = userId;
	}

	ElasticSearchAudioMessage visit(ElasticSearchAudioMessage elasticSearchAudioMessage) {
		String url = elasticSearchAudioMessage.getUrl();
		Long length = elasticSearchAudioMessage.getLength();
		String format = elasticSearchAudioMessage.getFormat();
		String id = elasticSearchAudioMessage.getId();
		Long date = elasticSearchAudioMessage.getDate();
		Long sender = elasticSearchAudioMessage.getSender();
		String conversationId = elasticSearchAudioMessage.getConversationId();
		String application = elasticSearchAudioMessage.getApplication();
		Set<Long> ignoredBy = elasticSearchAudioMessage.getIgnoredBy();

		ElasticSearchAudioMessage newElasticSearchAudioMessage = new ElasticSearchAudioMessage(id, sender, url, format,
				length, date, application, ignoredBy);
		newElasticSearchAudioMessage.setConversationId(conversationId);

		addReceiptsIfCorresponds(elasticSearchAudioMessage, sender, newElasticSearchAudioMessage);
		return newElasticSearchAudioMessage;
	}

	public ElasticSearchVideoMessage visit(ElasticSearchVideoMessage elasticSearchVideoMessage) {
		String url = elasticSearchVideoMessage.getUrl();
		Long length = elasticSearchVideoMessage.getLength();
		String format = elasticSearchVideoMessage.getFormat();
		String id = elasticSearchVideoMessage.getId();
		Long date = elasticSearchVideoMessage.getDate();
		Long sender = elasticSearchVideoMessage.getSender();
		String conversationId = elasticSearchVideoMessage.getConversationId();
		String thumbnail = elasticSearchVideoMessage.getThumbnail();
		String orientation = elasticSearchVideoMessage.getOrientation();
		String application = elasticSearchVideoMessage.getApplication();
		Set<Long> ignoredBy = elasticSearchVideoMessage.getIgnoredBy();

		ElasticSearchVideoMessage newElasticSearchVideoMessage = new ElasticSearchVideoMessage(id, sender, url,
				thumbnail, length, format, orientation, date, application, ignoredBy);
		newElasticSearchVideoMessage.setConversationId(conversationId);

		addReceiptsIfCorresponds(elasticSearchVideoMessage, sender, newElasticSearchVideoMessage);
		return newElasticSearchVideoMessage;
	}

	public ElasticSearchImageMessage visit(ElasticSearchImageMessage elasticSearchImageMessage) {
		String url = elasticSearchImageMessage.getUrl();
		String format = elasticSearchImageMessage.getFormat();
		String id = elasticSearchImageMessage.getId();
		Long date = elasticSearchImageMessage.getDate();
		Long sender = elasticSearchImageMessage.getSender();
		String thumbnail = elasticSearchImageMessage.getThumbnail();
		String orientation = elasticSearchImageMessage.getOrientation();
		String conversationId = elasticSearchImageMessage.getConversationId();
		String application = elasticSearchImageMessage.getApplication();
		Set<Long> ignoredBy = elasticSearchImageMessage.getIgnoredBy();

		ElasticSearchImageMessage newElasticSearchImageMessage = new ElasticSearchImageMessage(id, sender, url,
				thumbnail, format, orientation, date, application, ignoredBy);
		newElasticSearchImageMessage.setConversationId(conversationId);

		addReceiptsIfCorresponds(elasticSearchImageMessage, sender, newElasticSearchImageMessage);
		return newElasticSearchImageMessage;
	}

	public ElasticSearchTextMessage visit(ElasticSearchTextMessage elasticSearchTextMessage) {
		String id = elasticSearchTextMessage.getId();
		Long date = elasticSearchTextMessage.getDate();
		Long sender = elasticSearchTextMessage.getSender();
		String text = elasticSearchTextMessage.getText();
		String conversationId = elasticSearchTextMessage.getConversationId();
		String application = elasticSearchTextMessage.getApplication();
		Set<Long> ignoredBy = elasticSearchTextMessage.getIgnoredBy();

		ElasticSearchTextMessage newElasticSearchTextMessage = new ElasticSearchTextMessage(id, sender, text, date,
				application, ignoredBy);
		newElasticSearchTextMessage.setConversationId(conversationId);

		addReceiptsIfCorresponds(elasticSearchTextMessage, sender, newElasticSearchTextMessage);
		return newElasticSearchTextMessage;
	}

	private void addReceiptsIfCorresponds(ElasticSearchMessage elasticSearchMessage, Long sender,
			ElasticSearchMessage newElasticSearchMessage) {
		if (sender.equals(userId)) {
			List<ElasticSearchIndividualMessageReceipt> receipts = elasticSearchMessage.getReceipts();
			newElasticSearchMessage.addReceipts(receipts);
		}
	}
}
