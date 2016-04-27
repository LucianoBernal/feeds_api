package com.etermax.conversations.notification.model;

import com.etermax.conversations.model.User;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class NotificationResult {

	private String text;
	private List<User> receivers;

	public NotificationResult(String text, List<User> receivers) {
		this.text = text;
		this.receivers = receivers;
	}

	public String getText() {
		return text;
	}

	public List<User> getReceivers() {
		return receivers;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
