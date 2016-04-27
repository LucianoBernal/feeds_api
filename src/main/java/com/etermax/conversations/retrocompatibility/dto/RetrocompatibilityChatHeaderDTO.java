package com.etermax.conversations.retrocompatibility.dto;

import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.retrocompatibility.date.RetrocompatibilityDateParser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import retrocompatibility.dto.RetrocompatibilityUserDTO;

public class RetrocompatibilityChatHeaderDTO {

	@JsonProperty("user")
	private RetrocompatibilityUserDTO user;

	@JsonProperty("unread_count")
	private Long unreadCount;

	@JsonProperty("last_text")
	private String lastText;

	@JsonProperty("last_activity")
	private String lastActivity;

	@JsonIgnore
	private Long lastActivityDate;

	public RetrocompatibilityChatHeaderDTO(Long userId, ConversationDisplayDTO conversation,
			AddressedMessageDisplayDTO addressedMessageDisplayDTO, retrocompatibility.dto.RetrocompatibilityUserDTO userDTO, Long unreadCount) {
		this.user = userDTO;
		this.lastActivity = RetrocompatibilityDateParser.parseDate(addressedMessageDisplayDTO.getDate());
		this.unreadCount = unreadCount;
		this.lastText = addressedMessageDisplayDTO.getText();
		this.lastActivityDate = addressedMessageDisplayDTO.getDate();
	}

	public RetrocompatibilityUserDTO getUser() {
		return user;
	}

	public Long getUnreadCount() {
		return unreadCount;
	}

	public String getLastText() {
		return lastText;
	}

	public String getLastActivity() {
		return lastActivity;
	}

	public Long getLastActivityDate() {
		return lastActivityDate;
	}
}
