package com.etermax.conversations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DeleteConversationDisplayDTO {
	@JsonProperty("date") private Long deletionDate;

	public DeleteConversationDisplayDTO(Date deletionDate) {
		this.deletionDate = deletionDate.getTime();
	}

	public Long getDeletionDate() {
		return deletionDate;
	}
}
