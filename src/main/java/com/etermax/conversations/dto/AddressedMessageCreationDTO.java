package com.etermax.conversations.dto;

import com.etermax.conversations.error.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class AddressedMessageCreationDTO {
	@ApiModelProperty(required = true) @JsonProperty("text") private String text;

	@ApiModelProperty(required = true) @JsonProperty("application") private String application;

	@ApiModelProperty(required = true) @JsonProperty("sender_id") private Long senderId;

	@ApiModelProperty(required = true) @JsonProperty("receiver_id") private Long receiverId;

	@ApiModelProperty(required = false)
	private Boolean blocked = Boolean.FALSE;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getSenderId() {
		return senderId;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public void validate() throws InvalidDTOException {

		if (text == null || text.isEmpty()) {
			throw new InvalidDTOException(new EmptyMessageTextException(), "ConversationMessage text cannot be empty.");
		}

		if(application == null || application.isEmpty()){
			throw new InvalidDTOException(new EmptyApplicationException(), "Application cannot be empty");
		}

		if (senderId == null) {
			throw new InvalidDTOException(new EmptySenderException(), "Sender cannot be empty.");
		}
		if (receiverId == null) {
			throw new InvalidDTOException(new EmptyReceiverException(), "Receiver cannot be empty");
		}
		if (senderId < 1l || receiverId < 1l) {
			throw new InvalidDTOException(new InvalidUserException(new InvalidUserIdException()), "Invalid user.");
		}
		if (senderId.equals(receiverId)) {
			throw new InvalidDTOException(new EqualSenderReceiverException(), "Cannot send message to oneself");
		}
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	public Boolean getBlocked() {
		return blocked;
	}
}
