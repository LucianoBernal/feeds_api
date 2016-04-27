package com.etermax.conversations.dto;

import com.etermax.conversations.model.ConversationVideoMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoMessageDisplayDTO implements ConversationMessageDisplayDTO {
	@JsonProperty("id") private String id;

	@JsonProperty("sender_id") private Long senderId;

	@JsonProperty("date") private Long date;

	@JsonProperty("url") private String url;

	@JsonProperty("length") private Long length;

	@JsonProperty("preview") private String thumbnail;

	@JsonProperty("format") private String format;

	@JsonProperty("orientation") private String orientation;

	@JsonProperty("message_type") private String messageType;

	@JsonProperty("type") private String type;

	@JsonProperty("message_receipt") private MessageReceiptDTO messageReceipt;

	@JsonProperty("application") private String application;

	public VideoMessageDisplayDTO(ConversationVideoMessage conversationVideoMessage) {
		setSenderId(conversationVideoMessage.getSender().getId());
		setId(conversationVideoMessage.getId());
		setDate(conversationVideoMessage.getDate().getTime());
		setThumbnail(conversationVideoMessage.getThumbnail());
		setLength(conversationVideoMessage.getLength());
		setUrl(conversationVideoMessage.getUrl());
		setFormat(conversationVideoMessage.getFormat());
		setOrientation(conversationVideoMessage.getOrientation());
		setMessageReceipt(conversationVideoMessage);
		setApplication(conversationVideoMessage.getApplication());
		this.messageType = "video";
		this.type = "message";
	}

	private void setMessageReceipt(ConversationVideoMessage conversationMessage) {
		if (conversationMessage.getMessageReceipt() != null) {
			this.messageReceipt = new MessageReceiptDTO(conversationMessage.getMessageReceipt());
		}
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getMessageType() {
		return messageType;
	}

	public Long getLength() {
		return length;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Long getSenderId() {
		return senderId;
	}

	@Override
	public Long getDate() {
		return date;
	}

	@Override
	public void setDate(Long date) {
		this.date = date;
	}

	@Override
	public MessageReceiptDTO getMessageReceipt() {
		return messageReceipt;
	}

	@Override
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
