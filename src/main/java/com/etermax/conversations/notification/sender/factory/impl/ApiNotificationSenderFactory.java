package com.etermax.conversations.notification.sender.factory.impl;

import com.etermax.conversations.notification.api.NotificationsAPI;
import com.etermax.conversations.notification.model.NotificationSender;
import com.etermax.conversations.notification.sender.ApiNotificationSender;
import com.etermax.conversations.notification.sender.factory.NotificationSenderFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import retrofit.RestAdapter;

public class ApiNotificationSenderFactory implements NotificationSenderFactory {

	@JsonProperty("url")
	private String apiUrl;

	@Override
	public NotificationSender createSender() {
		return new ApiNotificationSender(getRestAdapter().create(NotificationsAPI.class));
	}

	public RestAdapter getRestAdapter() {
		return new RestAdapter.Builder().setEndpoint(apiUrl).build();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
