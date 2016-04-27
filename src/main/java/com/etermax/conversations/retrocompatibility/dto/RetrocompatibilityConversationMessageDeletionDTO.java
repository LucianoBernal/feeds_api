package com.etermax.conversations.retrocompatibility.dto;

import com.etermax.conversations.error.InvalidDTOException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RetrocompatibilityConversationMessageDeletionDTO {

	@JsonProperty("user1_id")
	private Long firstUserId;

	@JsonProperty("user2_id")
	private Long secondUserId;

	@JsonProperty("message_id")
	private String messageId;

	public Long getFirstUserId() {
		return firstUserId;
	}

	public Long getSecondUserId() {
		return secondUserId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void validate() throws InvalidDTOException {
		if(firstUserId == null || secondUserId == null || messageId == null){
			throw new InvalidDTOException(new Exception(), "");
		}
	}
}
