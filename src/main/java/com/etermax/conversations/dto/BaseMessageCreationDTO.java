package com.etermax.conversations.dto;

import com.etermax.conversations.model.MessageVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "message_type")
@JsonSubTypes({ @JsonSubTypes.Type(value = TextMessageCreationDTO.class, name = "text")})
public class BaseMessageCreationDTO {

	@JsonProperty("message_type") private String messageType;

	@JsonProperty("sender_id") private Long senderId;

	@JsonProperty("application") private String application;

	@JsonProperty(value = "ignored", defaultValue = "false") private Boolean ignored = false;

	public ConversationDataDTO accept(MessageVisitor visitor, String conversationId) {
		throw new RuntimeException("Wrong Message Creation Instantiation");
	}

	public String getApplication() { return application; }

	public Boolean getIgnored() {
		return ignored;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long sender) {
		this.senderId = sender;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) { this.messageType = messageType;}
}