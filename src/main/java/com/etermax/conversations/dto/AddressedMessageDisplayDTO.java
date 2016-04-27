package com.etermax.conversations.dto;

import com.etermax.conversations.model.AddressedMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressedMessageDisplayDTO implements ConversationDataDTO {
	@JsonProperty("message_id")
	private String id;

	@JsonProperty("text")
	private String text;

	@JsonProperty("application")
	private String application;

	@JsonProperty("sender_id")
	private Long senderId;

	@JsonProperty("receiver_id")
	private Long receiverId;

	@JsonProperty("date")
	private Long date;

	public AddressedMessageDisplayDTO(AddressedMessage savedConversationMessage) {
		setText(savedConversationMessage.getText());
		setSenderId(savedConversationMessage.getSender().getId());
		setReceiverId(savedConversationMessage.getUser().getId());
		setDate(savedConversationMessage.getDate().getTime());
		setId(savedConversationMessage.getId());
		setApplication(savedConversationMessage.getApplication());
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	private void setText(String text) {
		this.text = text;
	}

	public Long getSenderId() {
		return senderId;
	}

	private void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	private void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return "message";
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
