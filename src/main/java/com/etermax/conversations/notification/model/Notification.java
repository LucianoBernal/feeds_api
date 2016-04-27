package com.etermax.conversations.notification.model;

import com.etermax.conversations.model.TextFormatter;

public interface Notification {
	NotificationResult acceptSender(NotificationSender notificationSender, TextFormatter formatter);
}
