package com.etermax.conversations.notification.sender.factory.impl;

import com.etermax.conversations.notification.model.NotificationSender;
import com.etermax.conversations.notification.sender.NoneNotificationSender;
import com.etermax.conversations.notification.sender.factory.NotificationSenderFactory;

public class NoneNotificationSenderFactory implements NotificationSenderFactory {
	@Override
	public NotificationSender createSender() {
		return new NoneNotificationSender();
	}
}
