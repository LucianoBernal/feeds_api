package com.etermax.conversations.notification.model;

import com.etermax.conversations.model.User;

import java.util.List;

public interface NotificationSender {

	NotificationResult send(User sender, List<User> receivers, String text, String application, String messageId,
			String conversationId);

}
