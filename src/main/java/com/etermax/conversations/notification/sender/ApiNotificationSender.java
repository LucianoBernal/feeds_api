package com.etermax.conversations.notification.sender;

import com.etermax.conversations.model.User;
import com.etermax.conversations.notification.api.ApiNotification;
import com.etermax.conversations.notification.api.NotificationsAPI;
import com.etermax.conversations.notification.model.NotificationResult;
import com.etermax.conversations.notification.model.NotificationSender;
import rx.Observable;

import java.util.List;

public class ApiNotificationSender implements NotificationSender {

	private NotificationsAPI api;
	public ApiNotificationSender(NotificationsAPI api) {
		this.api = api;
	}

	@Override
	public NotificationResult send(User sender, List<User> receivers, String text, String application, String messageId,
			String conversationId) {
		return Observable.from(receivers)
						 .map(receiver -> createNotification(sender, text, receiver, application))
						 .flatMap(api::sendNotification)
						 .toList()
						 .map(apiNotifications -> new NotificationResult(text, receivers))
						 .toBlocking()
						 .single();
	}

	public ApiNotification createNotification(User sender, String text, User user, String application) {
		return new ApiNotification(sender.getId(), user.getId(), text, application);
	}

}
