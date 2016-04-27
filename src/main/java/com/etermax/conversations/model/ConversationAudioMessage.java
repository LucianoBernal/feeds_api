package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ConversationMessageMapperVisitor;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticSearchMessage;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class ConversationAudioMessage implements ConversationMessage {

	private Long length;
	private String url;
	private User sender;
	private String id;
	private String conversationId;
	private Date date;
	private String format;
	private MessageReceipt messageReceipt;
	private String application;
	private Boolean ignored;

	public ConversationAudioMessage(User sender, String conversationId, Date date, String url, Long length, String format,
			String application, Boolean ignored)
			throws InvalidMessageException {
		checkMessageContent(url, length, application);
		this.sender = sender;
		this.conversationId = conversationId;
		this.date = date;
		this.url = url;
		this.length = length;
		this.format = format;
		this.application = application;
		this.ignored = ignored;
	}

	@Override
	public ConversationDataDTO accept(ConversationDataDisplayVisitor conversationDataDisplayVisitor) {
		return conversationDataDisplayVisitor.visit(this);
	}

	private void checkMessageContent(String url, Long length, String application) throws InvalidMessageException {
		if (application == null || application.isEmpty() || url == null || url.isEmpty() && length <= 0) {
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

	@Override
	public Boolean getIgnored() {
		return ignored;
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
	public String getConversationId() {
		return conversationId;
	}

	public Long getLength() {
		return length;
	}

	public String getUrl() {
		return url;
	}

	public String getFormat() {
		return format;
	}

	@Override
	public String getApplication() {
		return application;
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
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
