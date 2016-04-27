package com.etermax.conversations.notification.sender.hermes;

import com.etermax.bagdes.service.BadgeService;
import com.etermax.conversations.model.User;
import com.etermax.conversations.notification.model.NotificationResult;
import com.etermax.conversations.notification.model.NotificationSender;
import com.etermax.conversations.notification.sender.hermes.model.notification.type.NewMessageNotification;
import com.etermax.hermes.common.notification.HermesNotificationReceiver;
import com.etermax.hermes.common.notification.HermesNotificationRemitter;
import com.etermax.hermes.common.notification.Notification;
import com.etermax.hermes.common.notification.dispatch.NotificationDispatcher;
import retrocompatibility.client.UsersRetrocompatibleClient;
import retrocompatibility.dto.RetrocompatibilityUserDTO;
import rx.Observable;

import java.util.List;

public class HermesNotificationSender implements NotificationSender {
	private BadgeService badgeService;
	private NotificationDispatcher dispatcher;
	private UsersRetrocompatibleClient usersRetrocompatibleClient;

	public HermesNotificationSender(NotificationDispatcher notificationDispatcher,
			UsersRetrocompatibleClient usersRetrocompatibleClient, BadgeService badgeService) {
		this.usersRetrocompatibleClient = usersRetrocompatibleClient;
		this.dispatcher = notificationDispatcher;
		this.badgeService = badgeService;
	}

	@Override
	public NotificationResult send(User sender, List<User> receivers, String text, String application, String messageId,
			String conversationId) {
		HermesNotificationRemitter notificationSender = createNotificationSender(
				sender);
		return Observable.from(receivers).map(receiver -> {
				HermesNotificationReceiver notificationReceiver = createNotificationReceiver(receiver, application);
		Notification notification = new NewMessageNotification(notificationSender, notificationReceiver, messageId, conversationId, text,
				application);
		dispatcher.dispatch(notification);
			return new NotificationResult(text,receivers);
		}).toBlocking()
				.last();
	}

	private HermesNotificationReceiver createNotificationReceiver(User receiver, String application) {
		return new HermesNotificationReceiver(receiver.getId(), getBadgeCount(receiver.getId(), application));
	}

	private Integer getBadgeCount(Long receiverId, String application) {
		badgeService.incrementChatNotificationBadge(receiverId, application);
		return badgeService.getBadgeCount(receiverId, application);
	}

	private HermesNotificationRemitter createNotificationSender(User sender) {
		RetrocompatibilityUserDTO user = usersRetrocompatibleClient.getUser(sender.getId(), sender.getId()).toBlocking().single();
		return new HermesNotificationRemitter(sender.getId(),
				user.getUsername(), user.getFbShowPicture(), user.getFacebookId());
	}
}
