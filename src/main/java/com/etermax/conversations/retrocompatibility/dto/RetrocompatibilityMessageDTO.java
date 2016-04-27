package com.etermax.conversations.retrocompatibility.dto;

import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityConversationAdapter;
import com.etermax.conversations.retrocompatibility.date.RetrocompatibilityDateParser;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetrocompatibilityMessageDTO {
	private static final Logger logger = LoggerFactory.getLogger(RetrocompatibilityConversationAdapter.class);

	@JsonProperty("id")
	private Long id;

	@JsonProperty("message")
	private String message;

	@JsonProperty("date")
	private String date;

	@JsonProperty("user_id")
	private Long userId;

	@JsonProperty("source_application")
	private String sourceApplication;

	@JsonProperty("type")
	private String type;

	public RetrocompatibilityMessageDTO(AddressedMessageDisplayDTO originalMessage, Integer id) {
		Long date = originalMessage.getDate();
		this.id = date;
		this.message = originalMessage.getText();
		this.date = RetrocompatibilityDateParser.parseDate(originalMessage.getDate());
		this.userId = originalMessage.getSenderId();
		this.sourceApplication = originalMessage.getApplication();
		this.type = "CHAT";
	}

	public Long getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public String getType() {
		return type;
	}

	public Long getUserId() {
		return userId;
	}

	public String getSourceApplication() {
		return sourceApplication;
	}

	public String getDate() {
		return date;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
