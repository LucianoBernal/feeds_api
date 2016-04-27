package com.etermax.conversations.dto;

import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.model.MessageVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AudioMessageCreationDTO extends BaseMessageCreationDTO {

	@JsonProperty("sender_id") private Long senderId;

	@JsonProperty("application") private String application;

	@JsonProperty("url") private String url;

	@JsonProperty("length") private Long length;

	@JsonProperty("format") private String format;

	public AudioMessageCreationDTO() {
		setMessageType();
	}

	@Override
	public Long getSenderId() {
		return senderId;
	}

	@Override
	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setMessageType() { super.setMessageType("audio");}

	public ConversationDataDTO accept(MessageVisitor visitor, String conversationId) {
		return visitor.saveMessage(this, conversationId);
	}

	public void validate() throws InvalidDTOException {
		if (senderId == null || application == null || application.isEmpty() || length == null || url == null || url.isEmpty()) {
			throw new InvalidDTOException(new Exception(), "");
		}
	}
}
