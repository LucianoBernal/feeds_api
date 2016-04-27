package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.model.ConversationData;

public interface ElasticSearchConversationData {
	String getType();
	String getConversationId();
	String getId();
	String getApplication();
	Long getDate();
	ConversationData accept(ElasticSearchDataMapperVisitor elasticSearchDataMapperVisitor);
	void setId(String id);
}

