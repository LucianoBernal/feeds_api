package com.etermax.conversations.repository.impl.elasticsearch.domain;

public class ElasticSearchConversationDeletion {

	private String application;
	private String conversationId;
	private Long userId;
	private Long date;

	public ElasticSearchConversationDeletion(String application, String conversationId, Long userId, Long date) {
		this.application = application;
		this.conversationId = conversationId;
		this.userId = userId;
		this.date = date;
	}

	public Long getDate() {
		if(date == null) return 0L;
		return date;
	}

	public String getConversationId() {
		return conversationId;
	}
}
