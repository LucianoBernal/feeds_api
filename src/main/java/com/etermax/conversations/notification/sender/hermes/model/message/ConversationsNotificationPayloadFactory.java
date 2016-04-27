package com.etermax.conversations.notification.sender.hermes.model.message;

import java.util.Map;

import com.etermax.hermes.common.notification.Notification;
import com.etermax.hermes.common.notification.NotificationPayloadFactory;
import com.etermax.hermes.common.notification.NotificationRemitter;
import com.etermax.hermes.devices.model.DeviceType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public abstract class ConversationsNotificationPayloadFactory implements NotificationPayloadFactory {

	private static Map<DeviceType, PayloadResolver> PAYLOAD_RESOLVER = ImmutableMap.<DeviceType, PayloadResolver>builder()
			.put(DeviceType.ANDROID, new PayloadResolver() {
				@Override
				public String apply(ConversationsNotificationPayloadFactory factory) {
					return factory.createAndroidPayload();
				}
			}).put(DeviceType.IPHONE, new PayloadResolver() {
				@Override
				public String apply(ConversationsNotificationPayloadFactory factory) {
					return factory.createIosPayload();
				}
			}).put(DeviceType.BROWSER, new PayloadResolver() {
				@Override
				public String apply(ConversationsNotificationPayloadFactory factory) {
					return factory.createBrowserPayload();
				}
			}).build();
	private NotificationRemitter notificationRemitter;
	private Long receiverId;
	private Long gameId;
	private Integer badgeCount;

	protected ConversationsNotificationPayloadFactory(Notification notification) {
		this.notificationRemitter = notification.getRemitter();
		this.receiverId = notification.getReceiver().getId();
		this.gameId = notification.getGameId();
		this.badgeCount = notification.getReceiver().getBadgeCount();
	}

	public NotificationRemitter getNotificationRemitter() {
		return notificationRemitter;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public Long getGameId() {
		return gameId;
	}

	public Integer getBadgeCount() {
		return badgeCount;
	}

	public Optional<String> createPayloadFor(DeviceType deviceType) {
		Function<ConversationsNotificationPayloadFactory, String> resolver = PAYLOAD_RESOLVER.get(deviceType);
		Optional<String> payload = Optional.absent();
		if (resolver != null) {
			payload = Optional.of(resolver.apply(this));
		}
		return payload;
	}

	protected abstract String createAndroidPayload();

	protected abstract String createIosPayload();

	protected abstract String createBrowserPayload();

	private interface PayloadResolver extends Function<ConversationsNotificationPayloadFactory, String> {
	}

}
