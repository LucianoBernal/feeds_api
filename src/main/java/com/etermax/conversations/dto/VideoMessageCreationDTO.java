package com.etermax.conversations.dto;

import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.model.MessageVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoMessageCreationDTO extends BaseMessageCreationDTO {

	@JsonProperty("sender_id") private Long senderId;

	@JsonProperty("url") private String url;

	@JsonProperty("format") private String format;

	@JsonProperty("preview") private String thumbnail;

	@JsonProperty("length") private Long length;

	@JsonProperty("orientation") private String orientation;

	@JsonProperty("application") private String application;

	public VideoMessageCreationDTO() {
		setMessageType();
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getFormat() {
		return format;
	}

	@Override
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public Long getSenderId() {
		return senderId;
	}

	@Override
	public void setSenderId(Long sender) {
		this.senderId = sender;
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

	public void setMessageType() { super.setMessageType("video");}

	public ConversationDataDTO accept(MessageVisitor visitor, String conversationId) {
		return visitor.saveMessage(this, conversationId);
	}

	public void validate() throws InvalidDTOException {
		if (application == null || application.isEmpty() || senderId == null || url == null || url.isEmpty()
				|| thumbnail == null || thumbnail.isEmpty() || length == null || orientation == null || orientation
				.isEmpty()) {
			throw new InvalidDTOException(new Exception(), "");
		}
	}

}
