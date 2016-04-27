package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.model.ConversationData;

import java.util.List;

public class ElasticsearchMessageReceipt implements ElasticSearchConversationData {

	private String id;
	private String conversationId;
	private List<ElasticSearchIndividualMessageReceipt> filteredRceipts;
	private String application;

	public ElasticsearchMessageReceipt(String id, String conversationId,
			List<ElasticSearchIndividualMessageReceipt> filteredRceipts, String application) {
		this.id = id;
		this.conversationId = conversationId;
		this.filteredRceipts = filteredRceipts;
		this.application = application;
	}

	public String getId() {
		return id;
	}

	@Override
	public Long getDate() {
		return filteredRceipts.get(filteredRceipts.size() - 1).getDate();
	}

	@Override
	public ConversationData accept(ElasticSearchDataMapperVisitor elasticSearchDataMapperVisitor) {
		return elasticSearchDataMapperVisitor.visit(this);
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getApplication() {
		return application;
	}

	@Override

	public String getType() {
		return "receipt";
	}

	@Override
	public String getConversationId() {
		return conversationId;
	}

	public List<ElasticSearchIndividualMessageReceipt> getReceipts() {
		return filteredRceipts;
	}
}
