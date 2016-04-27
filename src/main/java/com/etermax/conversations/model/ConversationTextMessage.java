package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ConversationMessageMapperVisitor;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticSearchMessage;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class ConversationTextMessage implements ConversationMessage {

	private User sender;
	private String id;
	private String conversationId;
	private Date date;
	private String text;

	private MessageReceipt messageReceipt;
	private String application;
	private Boolean ignored;

	public ConversationTextMessage(User sender, String conversationId, Date date, String text, String application,
			Boolean ignored)
			throws InvalidMessageException {
		checkMessageContent(text);
		this.sender = sender;
		this.conversationId = conversationId;
		this.date = date;
		this.text = text;
		this.application = application;
		this.ignored = ignored;
	}

	private void checkMessageContent(String text) throws InvalidMessageException {
		if (text == null || text.isEmpty()) {
			throw new InvalidMessageException(new Exception());
		}
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public String getType() {
		return "message";
	}

	public String getText() {
		return text;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public User getSender() {
		return sender;
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
	public String getConversationId() {
		return conversationId;
	}

	@Override
	public ConversationDataDTO accept(ConversationDataDisplayVisitor conversationDataDisplayVisitor) {
		return conversationDataDisplayVisitor.visit(this);
	}

	@Override
	public void addMessageReceipt(MessageReceipt messageReceipt) {
		this.messageReceipt = messageReceipt;
	}

	@Override
	public MessageReceipt getMessageReceipt() {
		return messageReceipt;
	}

	@Override
	public ElasticSearchMessage accept(ConversationMessageMapperVisitor conversationMessageMapperVisitor) {
		return conversationMessageMapperVisitor.visit(this);
	}

	@Override
	public String acceptFormatter(TextFormatter textFormatter) {
		return textFormatter.format(this);
	}

	@Override
	public Boolean getIgnored() {
		return ignored;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
