package com.etermax.conversations.retrocompatibility.dto;

import com.etermax.conversations.error.InvalidDTOException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RetrocompatibilityConversationDeletionDTO {

	@JsonProperty("user1_id")
	private Long firstUserId;

	@JsonProperty("user2_id")
	private Long secondUserId;

	@JsonProperty("application")
	private String application;

	public Long getFirstUserId() {
		return firstUserId;
	}

	public Long getSecondUserId() {
		return secondUserId;
	}

	public String getApplication() {
		return application;
	}

	public void validate() throws InvalidDTOException {
		if(firstUserId == null || secondUserId == null || application == null){
			throw new InvalidDTOException(new Exception(), "");
		}
	}
}
