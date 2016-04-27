package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.List;

public class ElasticsearchMessageList {

	private List<ElasticSearchMessage> messages;
	private Long conversationId;
	private Long total;

	public ElasticsearchMessageList(List<ElasticSearchMessage> messages, Long conversationId, Long total) {
		this.messages = messages;
		this.conversationId = conversationId;
		this.total = total;
	}

	public List<ElasticSearchMessage> getMessages() {
		return messages;
	}

	public Long getTotal() {
		return total;
	}

	public Long getConversationId() {
		return conversationId;
	}

}
