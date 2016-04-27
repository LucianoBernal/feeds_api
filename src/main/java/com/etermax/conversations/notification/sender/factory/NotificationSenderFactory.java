package com.etermax.conversations.notification.sender.factory;

import com.etermax.conversations.notification.model.NotificationSender;
import com.etermax.conversations.notification.sender.factory.impl.ApiNotificationSenderFactory;
import com.etermax.conversations.notification.sender.factory.impl.HermesNotificationSenderFactory;
import com.etermax.conversations.notification.sender.factory.impl.NoneNotificationSenderFactory;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = NoneNotificationSenderFactory.class, name = "disabled"),
		@JsonSubTypes.Type(value = ApiNotificationSenderFactory.class, name = "api"),
		@JsonSubTypes.Type(value = HermesNotificationSenderFactory.class, name = "hermes")
})
public interface NotificationSenderFactory {

	NotificationSender createSender();

}
