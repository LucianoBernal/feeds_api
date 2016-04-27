package com.etermax.conversations.dto;

import com.etermax.conversations.error.InvalidDTOException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IndividualMessageReceiptCreationDTO {
	@JsonProperty("type")
	private String type;
	@JsonProperty("user_id")
	private Long userId;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void validate() throws InvalidDTOException {
		if(userId == null || userId.equals(0l)){
			throw new InvalidDTOException(null, "");
		}
		if(type == null || type.isEmpty()){
			throw new InvalidDTOException(null, "");
		}
	}
}
