package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ConversationMessageMapperVisitor;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticSearchMessage;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class ConversationImageMessage implements ConversationMessage {
	private final String format;

	private String id;
	private Date date;
	private String thumbnail;
	private String url;
	private User sender;
	private String conversationId;
	private MessageReceipt messageReceipt;
	private String orientation;
	private String application;
	private Boolean ignored;

	public ConversationImageMessage(User sender, String conversationId, Date date, String url, String thumbnail,
			String format, String orientation, String application, Boolean ignored)
			throws InvalidMessageException {
		checkMessageContent(url, thumbnail, orientation, application);
		this.sender = sender;
		this.conversationId = conversationId;
		this.date = date;
		this.url = url;
		this.thumbnail = thumbnail;
		this.format = format;
		this.orientation = orientation;
		this.application = application;
		this.ignored = ignored;
	}

	private void checkMessageContent(String url, String thumbnail, String orientation, String application) throws InvalidMessageException {
		if ((application == null || application.isEmpty()) || (url == null || url.isEmpty()) || ((thumbnail == null || thumbnail.isEmpty())) || ((orientation == null || orientation.isEmpty()))) {
			throw new InvalidMessageException(new Exception());
		}
	}

	public User getSender() {
		return sender;
	}

	public String getFormat() {
		return format;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	public String getApplication() {
		return application;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String getType() {
		return "message";
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public String getUrl() {
		return url;
	}

	public String getOrientation() {
		return orientation;
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
	public Boolean getIgnored() {
		return ignored;
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
