package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ConversationMessageMapperVisitor;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticSearchMessage;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class ConversationVideoMessage implements ConversationMessage {
	private final String format;

	private User sender;
	private String id;
	private String conversationId;
	private Date date;
	private String url;
	private String thumbnail;
	private Long length;
	private MessageReceipt messageReceipt;
	private String orientation;
	private String application;
	private Boolean ignored;

	public ConversationVideoMessage(User sender, String conversationId, Date date, String url, String thumbnail,
			Long length, String format, String orientation, String application, Boolean ignored) throws InvalidMessageException {
		checkMessageContent(url, thumbnail, length, orientation, application);
		this.sender = sender;
		this.conversationId = conversationId;
		this.date = date;
		this.url = url;
		this.thumbnail = thumbnail;
		this.length = length;
		this.format = format;
		this.orientation = orientation;
		this.application = application;
		this.ignored = ignored;

	}

	@Override
	public ConversationDataDTO accept(ConversationDataDisplayVisitor conversationDataDisplayVisitor) {
		return conversationDataDisplayVisitor.visit(this);
	}

	private void checkMessageContent(String url, String thumbnail, Long length, String orientation, String application)
			throws InvalidMessageException {
		if ((application == null || application.isEmpty()) || (url == null || url.isEmpty()) || (
				(thumbnail == null || thumbnail.isEmpty()) || (length == null) || (orientation == null || orientation
						.isEmpty()))) {
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

	public String getUrl() {
		return url;
	}

	public String getFormat() {
		return format;
	}

	public String getOrientation() {
		return orientation;
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
	public Boolean getIgnored() {
		return this.ignored;
	}

	@Override
	public String getConversationId() {
		return conversationId;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	@Override
	public String getApplication() {
		return application;
	}

	public Long getLength() {
		return length;
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
