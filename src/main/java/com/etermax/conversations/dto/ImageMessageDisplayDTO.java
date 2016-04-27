package com.etermax.conversations.dto;

import com.etermax.conversations.model.ConversationImageMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageMessageDisplayDTO implements ConversationMessageDisplayDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("sender_id")
	private Long senderId;

	@JsonProperty("date")
	private Long date;

	@JsonProperty("url")
	private String url;

	@JsonProperty("preview")
	private String thumbnail;

	@JsonProperty("format")
	private String format;

	@JsonProperty("orientation")
	private String orientation;

	@JsonProperty("message_type")
	private String messageType;

	@JsonProperty("application")
	private String application;

	@JsonProperty("type")
	private String type;

	@JsonProperty("message_receipt")
	private MessageReceiptDTO messageReceipt;

	public ImageMessageDisplayDTO(ConversationImageMessage conversationImageMessage) {
		setSenderId(conversationImageMessage.getSender().getId());
		setId(conversationImageMessage.getId());
		setDate(conversationImageMessage.getDate().getTime());
		setThumbnail(conversationImageMessage.getThumbnail());
		setUrl(conversationImageMessage.getUrl());
		setFormat(conversationImageMessage.getFormat());
		setOrientation(conversationImageMessage.getOrientation());
		setMessageReceipt(conversationImageMessage);
		setApplication(conversationImageMessage.getApplication());
		this.messageType = "image";
		this.type = "message";
	}

	private void setMessageReceipt(ConversationImageMessage conversationMessage) {
		if(conversationMessage.getMessageReceipt() != null) {
			this.messageReceipt = new MessageReceiptDTO(conversationMessage.getMessageReceipt());
		}
	}

	@Override
	public MessageReceiptDTO getMessageReceipt() {
		return messageReceipt;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setOrientation(String orientation) {this.orientation = orientation; }

	public String getMessageType() {
		return messageType;
	}

	@Override
	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOrientation() {
		return orientation;
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
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {

		this.application = application;
	}
}