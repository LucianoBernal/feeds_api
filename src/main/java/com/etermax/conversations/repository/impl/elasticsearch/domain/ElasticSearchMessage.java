package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.List;
import java.util.Set;

public interface ElasticSearchMessage extends ElasticSearchConversationData {
	void setConversationId(String conversationId);

	String getType();

	String getConversationId();

	String getId();

	Long getSender();

	String getApplication();

	Long getDate();

	List<ElasticSearchIndividualMessageReceipt> getReceipts();

	List<Long> deletedBy();

	ElasticSearchMessage accept(ElasticReceiptVisitor elasticReceiptVisitor);

	void addReceipts(List<ElasticSearchIndividualMessageReceipt> individualReceipts);

	Set<Long> getIgnoredBy();
}
