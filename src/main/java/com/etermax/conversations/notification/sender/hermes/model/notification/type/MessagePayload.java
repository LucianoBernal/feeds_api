package com.etermax.conversations.notification.sender.hermes.model.notification.type;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MessagePayload {

	@JsonProperty("id")
	private String messageId;

	@JsonProperty("user_id")
	private Long senderId;

	@JsonProperty("date")
	private Date date;

	@JsonProperty("text")
	private String text;

	MessagePayload(String messageId, long senderId, Date date, String text) {
		checkArgument(!messageId.isEmpty(), "messageId mustn't be empty");
		checkArgument(senderId > 0, "senderId must be greater than zero");
		checkNotNull(text, "message's text can not be empty");
		checkNotNull(date, "The message's date can not be null");
		this.messageId = messageId;
		this.senderId = senderId;
		this.date = date;
		this.text = text;
	}

	public String getMessageId() {
		return messageId;
	}

	public Long getSenderId() {
		return senderId;
	}

	public Date getDate() {
		return date;
	}

	public String getText() {
		return text;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String messageId;

		private long senderId;

		private Date date;

		private String text;

		public Builder withMessageId(String messageId) {
			this.messageId = messageId;
			return this;
		}

		public Builder withSenderId(long senderId) {
			this.senderId = senderId;
			return this;
		}

		public Builder withDate(Date date) {
			this.date = date;
			return this;
		}

		public Builder withText(String text) {
			this.text = text;
			return this;
		}

		public MessagePayload build() {
			return new MessagePayload(messageId, senderId, date, text);
		}
	}
}

