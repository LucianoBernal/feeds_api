package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.model.ConversationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ElasticSearchTextMessage implements ElasticSearchMessage {

	private String id;
	private String conversationId;
	private Long sender;
	private String text;
	private Long date;
	private String application;

	private Set<Long> ignoredBy;

	private String type;
	private List<Long> deletedBy;
	private List<ElasticSearchIndividualMessageReceipt> receipts;
	public ElasticSearchTextMessage(String id, Long sender, String text, Long date, String application, Set<Long> ignoredBy) {
		this.id = id;
		this.sender = sender;
		this.text = text;
		this.date = date;
		this.application = application;
		this.ignoredBy = ignoredBy;
		this.type = "text";
		this.deletedBy = new ArrayList<>();
		this.receipts = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public String getConversationId() {
		return conversationId;
	}

	public Long getSender() {
		return sender;
	}

	public String getText() {
		return text;
	}

	public Long getDate() {
		return date;
	}

	@Override
	public List<ElasticSearchIndividualMessageReceipt> getReceipts() {
		return receipts;
	}

	@Override
	public List<Long> deletedBy() {
		return this.deletedBy;
	}

	@Override
	public ElasticSearchMessage accept(ElasticReceiptVisitor elasticReceiptVisitor) {
		return elasticReceiptVisitor.visit(this);
	}

	@Override
	public void addReceipts(List<ElasticSearchIndividualMessageReceipt> individualReceipts) {
		this.receipts = individualReceipts;
	}

	@Override
	public ConversationMessage accept(ElasticSearchDataMapperVisitor elasticSearchDataMapperVisitor) {
		return elasticSearchDataMapperVisitor.visit(this);
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	@Override
	public String getType() {
		return "text";
	}

	@Override
	public String getApplication() {
		return application;
	}

	@Override
	public Set<Long> getIgnoredBy() {
		return ignoredBy;
	}
}
