package com.etermax.conversations.factory;

import com.etermax.conversations.model.RetrocompatibilityTextFormatter;
import com.etermax.conversations.notification.sender.factory.NotificationSenderFactory;
import com.etermax.conversations.notification.service.NotificationService;

public class NotificationServiceFactory {

	private NotificationSenderFactory senderFactory;

	public NotificationServiceFactory(NotificationSenderFactory senderFactory) {
		this.senderFactory = senderFactory;
	}

	public NotificationService createNotificationService() {
		return new NotificationService(new RetrocompatibilityTextFormatter(), senderFactory.createSender());
	}

}
