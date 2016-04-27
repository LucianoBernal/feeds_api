package com.etermax.conversations.dto;

import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.model.MessageVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextMessageCreationDTO extends BaseMessageCreationDTO {

	@JsonProperty("sender_id") private Long senderId;

	@JsonProperty("text") private String text;

	@JsonProperty("application") private String application;

	public TextMessageCreationDTO() {
		setMessageType();
	}

	public ConversationDataDTO accept(MessageVisitor visitor, String conversationId) {
		return visitor.saveMessage(this, conversationId);
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public void setMessageType() { super.setMessageType("text");}

	public void validate() throws InvalidDTOException {
		if (application == null || application.isEmpty() || senderId == null || text == null || text.isEmpty()) {
			throw new InvalidDTOException(new Exception(), "");
		}
	}
}
