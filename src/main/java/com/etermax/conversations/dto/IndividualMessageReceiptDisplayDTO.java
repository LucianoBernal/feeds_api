package com.etermax.conversations.dto;

import com.etermax.conversations.model.IndividualMessageReceipt;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IndividualMessageReceiptDisplayDTO {

	@JsonProperty("type")
	private String type;

	@JsonProperty("date")
	private Long timestamp;

	@JsonProperty("user_id")
	private Long userId;

	public IndividualMessageReceiptDisplayDTO(IndividualMessageReceipt individualMessageReceipt) {
		setType(individualMessageReceipt.getType().toString());
		setTimestamp(individualMessageReceipt.getDate().getTime());
		setUserId(individualMessageReceipt.getUser());

	}

	public IndividualMessageReceiptDisplayDTO() {
	}

	public String getType() {
		return type;
	}

	private void setType(String type) {
		this.type = type;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	private void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Long getUserId() {
		return userId;
	}

	private void setUserId(Long userId) { this.userId = userId; }

}
