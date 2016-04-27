package com.etermax.conversations.dto;

import com.etermax.conversations.model.ConversationAudioMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AudioMessageDisplayDTO implements ConversationMessageDisplayDTO {

	@JsonProperty("id") private String id;

	@JsonProperty("sender_id") private Long senderId;

	@JsonProperty("date") private Long date;

	@JsonProperty("length") private Long length;

	@JsonProperty("url") private String url;

	@JsonProperty("format") private String format;

	@JsonProperty("application") private String application;

	@JsonProperty("message_type") private String messageType;

	@JsonProperty("type") private String type;

	@JsonProperty("message_receipt") private MessageReceiptDTO messageReceipt;

	public AudioMessageDisplayDTO(ConversationAudioMessage conversationMessage) {
		setSenderId(conversationMessage.getSender().getId());
		setId(conversationMessage.getId());
		setDate(conversationMessage.getDate().getTime());
		setLength(conversationMessage.getLength());
		setUrl(conversationMessage.getUrl());
		setFormat(conversationMessage.getFormat());
		setMessageReceipt(conversationMessage);
		setApplication(conversationMessage.getApplication());
		this.messageType = "audio";
		this.type = "message";

	}

	private void setMessageReceipt(ConversationAudioMessage conversationMessage) {
		if(conversationMessage.getMessageReceipt() != null) {
			this.messageReceipt = new MessageReceiptDTO(conversationMessage.getMessageReceipt());
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessageType() {
		return messageType;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
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

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setDate(Long date) {
		this.date = date;
	}

	@Override
	public MessageReceiptDTO getMessageReceipt() {
		return this.messageReceipt;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
