package com.etermax.conversations.dto;

import com.etermax.conversations.model.ConversationTextMessage;
import com.etermax.conversations.model.MessageReceipt;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextMessageDisplayDTO implements ConversationMessageDisplayDTO {

	@JsonProperty("id") private String id;

	@JsonProperty("sender_id") private Long senderId;

	@JsonProperty("date") private Long date;

	@JsonProperty("text") private String text;

	@JsonProperty("message_type") private String messageType;

	@JsonProperty("type") private String type;

	@JsonProperty("message_receipt")
	private MessageReceiptDTO messageReceipt;
	private String application;

	public TextMessageDisplayDTO(ConversationTextMessage conversationMessage) {
		setSenderId(conversationMessage.getSender().getId());
		setId(conversationMessage.getId());
		setDate(conversationMessage.getDate().getTime());
		setText(conversationMessage.getText());
		setApplication(conversationMessage.getApplication());
		setMessageReceipt(conversationMessage.getMessageReceipt());
		this.messageType = "text";
		this.type = "message";
	}

	private void setMessageReceipt(MessageReceipt messageReceipt) {
		if(!(messageReceipt == null)) {
			this.messageReceipt = new MessageReceiptDTO(messageReceipt);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getType() {
		return type;
	}

	public String getMessageType() {
		return messageType;
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
		return messageReceipt;
	}

	@Override
	public String getApplication() {
		return application;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
