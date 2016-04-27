package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.model.ConversationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ElasticSearchAudioMessage implements ElasticSearchMessage {
	private Long date;
	private String application;
	private Set<Long> ignoredBy;
	private String url;
	private String format;
	private Long length;
	private Long sender;
	private String id;
	private String conversationId;
	private String type;
	private List<Long> deletedBy;
	private List<ElasticSearchIndividualMessageReceipt> receipts;

	public ElasticSearchAudioMessage(String id, Long sender, String url, String format, Long length, Long date,
			String application, Set<Long> ignoredBy) {
		this.id = id;
		this.sender = sender;
		this.url = url;
		this.format = format;
		this.length = length;
		this.date = date;
		this.application = application;
		this.type = "audio";
		this.deletedBy = new ArrayList<>();
		this.receipts = new ArrayList<>();
		this.ignoredBy = ignoredBy;
	}

	@Override
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	@Override
	public String getType() {
		return "audio";
	}

	@Override
	public String getConversationId() {
		return conversationId;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Long getSender() {
		return sender;
	}

	@Override
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

	public String getUrl() {
		return url;
	}

	public Long getLength() {
		return length;
	}

	public String getFormat() {
		return format;
	}

	public String getApplication() {
		return application;
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
	public Set<Long> getIgnoredBy() {
		return ignoredBy;
	}
}
