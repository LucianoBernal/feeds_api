package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.model.ConversationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ElasticSearchVideoMessage implements ElasticSearchMessage {
	private String id;
	private Long sender;
	private String url;
	private String thumbnail;
	private Long length;
	private String format;
	private String orientation;
	private Long date;
	private String application;
	private Set<Long> ignoredBy;
	private String conversationId;
	private String type;
	private List<Long> deletedBy;
	private List<ElasticSearchIndividualMessageReceipt> receipts;

	public ElasticSearchVideoMessage(String id, Long sender, String url, String thumbnail, Long length, String format,
			String orientation, Long date, String application, Set<Long> ignoredBy) {
		this.id = id;
		this.sender = sender;
		this.url = url;
		this.thumbnail = thumbnail;
		this.length = length;
		this.format = format;
		this.orientation = orientation;
		this.date = date;
		this.application = application;
		this.ignoredBy = ignoredBy;
		this.type = "video";
		this.deletedBy = new ArrayList<>();
		this.receipts = new ArrayList<>();
	}

	@Override
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	@Override
	public String getType() {
		return "video";
	}

	@Override
	public String getConversationId() {
		return conversationId;
	}

	@Override
	public Set<Long> getIgnoredBy() {
		return ignoredBy;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getApplication() {
		return application;
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

	@Override
	public ConversationMessage accept(ElasticSearchDataMapperVisitor elasticSearchDataMapperVisitor) {
		return elasticSearchDataMapperVisitor.visit(this);
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public String getOrientation() {
		return orientation;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public Long getLength() {
		return length;
	}

	public String getFormat() {
		return format;
	}
}
