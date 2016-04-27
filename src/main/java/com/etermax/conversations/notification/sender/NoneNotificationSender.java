package com.etermax.conversations.notification.sender;

import com.etermax.conversations.notification.model.NotificationResult;
import com.etermax.conversations.notification.model.NotificationSender;
import com.etermax.conversations.model.User;

import java.util.List;

public class NoneNotificationSender implements NotificationSender {

	@Override
	public NotificationResult send(User sender, List<User> receivers, String text, String application, String messageId,
			String conversationId) {
		return new NotificationResult(text, receivers);
	}
}
