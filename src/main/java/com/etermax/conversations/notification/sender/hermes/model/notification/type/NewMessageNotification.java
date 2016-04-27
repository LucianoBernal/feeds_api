package com.etermax.conversations.notification.sender.hermes.model.notification.type;

import com.etermax.hermes.common.notification.Notification;
import com.etermax.hermes.common.notification.NotificationPayloadFactory;
import com.etermax.hermes.common.notification.NotificationReceiver;
import com.etermax.hermes.common.notification.NotificationRemitter;

public class NewMessageNotification extends Notification {

	/**
	 * Las versiones viejas reciben un game id ya que los mensajes estaban relacionados a un juego. Los clientes
	 * actuales no tienen esa dependencia
	 */
	private static final long FAKE_GAME_ID = 0L;

	private String messageId;
	private String conversationId;
	private String message;

	private NewMessagePayloadFactory newMessagePayloadFactory;

	public NewMessageNotification(NotificationRemitter notificationSender, NotificationReceiver notificationReceiver,
			String messageId, String conversationId, String message, String application) {
		super(notificationSender, notificationReceiver, FAKE_GAME_ID, application);
		this.messageId = messageId;
		this.conversationId = conversationId;
		this.message = message;
		newMessagePayloadFactory = new NewMessagePayloadFactory(this);
	}

	@Override
	public NotificationPayloadFactory getNotificationPayloadFactory() {
		return newMessagePayloadFactory;
	}

	String getMessage() {
		return message;
	}

	String getMessageId() {
		return messageId;
	}

	public String getConversationId() {
		return conversationId;
	}
}