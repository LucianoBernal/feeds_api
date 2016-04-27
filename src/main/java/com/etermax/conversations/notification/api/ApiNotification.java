package com.etermax.conversations.notification.api;

import com.google.gson.annotations.SerializedName;

public class ApiNotification {

	@SerializedName("sender_id")
	private Long senderId;
	@SerializedName("receiver_id")
	private Long receiverId;
	@SerializedName("message")
	private String text;
	@SerializedName("application")
	private String application;

	public ApiNotification(Long senderId, Long receiverId, String text, String application) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.text = text;
		this.application = application;
	}
}
