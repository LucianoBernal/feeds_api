package com.etermax.conversations.dto;

import com.etermax.conversations.error.*;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversationMessageDeletionDTO {

	@JsonProperty("user_id")
	private Long userId;

	@JsonProperty("application")
	private String application;

	public void validate() throws InvalidDTOException {
		if (userId == null) {
			throw new InvalidDTOException(new EmptyUserException(), "User cannot be empty.");
		}
		if (userId < 1l) {
			throw new InvalidDTOException(new InvalidUserException(new InvalidUserIdException()), "Invalid user");
		}
		if (application == null) {
			throw new InvalidDTOException(new EmptyApplicationException(), "Application cannot be empty.");
		}
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
