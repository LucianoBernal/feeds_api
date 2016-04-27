package com.etermax.conversations.notification.sender.hermes.model.notification.type;

import java.util.Date;

import com.etermax.conversations.notification.sender.hermes.model.message.ConversationsNotificationPayloadFactory;
import com.etermax.conversations.notification.sender.hermes.model.notification.builder.IosSocialPayloadCategories;
import com.etermax.hermes.common.notification.builder.android.AndroidPayloadBuilder;
import com.etermax.hermes.common.notification.builder.browser.BrowserNotificationType;
import com.etermax.hermes.common.notification.builder.browser.BrowserPayloadBuilder;
import com.etermax.hermes.common.notification.builder.ios.IosPayloadBuilder;

public class NewMessagePayloadFactory extends ConversationsNotificationPayloadFactory {
	private static final String NEW_MESSAGE = "NEW_MESSAGE";

	private String conversationId;

	private String chatMessage;
	private String messageId;
	public NewMessagePayloadFactory(NewMessageNotification newMessageNotification) {
		super(newMessageNotification);
		chatMessage = newMessageNotification.getMessage();
		messageId = newMessageNotification.getMessageId();
		conversationId = newMessageNotification.getConversationId();
	}

	@Override
	protected String createAndroidPayload() {
		return new AndroidPayloadBuilder().type(NEW_MESSAGE).message(getChatMessage()).gameId(getGameId().toString())
				.senderName(getNotificationRemitter().getName()).messageId(String.valueOf(getMessageId()))
				.showSenderFacebookPicture(String.valueOf(getNotificationRemitter().isShowSenderFacebookPicture()))
				.senderFacebookId(getNotificationRemitter().getSenderFacebookId()).senderId(String.valueOf(getNotificationRemitter().getSenderId()))
				.conversationId(getConversationId()).build();
	}

	public String getConversationId() {
		return conversationId;
	}

	private String getMessageId() {
		return messageId;
	}

	private String getChatMessage() {
		return chatMessage;
	}

	@Override
	protected String createIosPayload() {
		return new IosPayloadBuilder(new IosSocialPayloadCategories()).game(getGameId().toString())
				.sender(String.valueOf(getNotificationRemitter().getSenderId())).locAarg(getNotificationRemitter().getName())
				.locAarg(getChatMessage()).notificationType(NEW_MESSAGE).messageId(String.valueOf(getMessageId())).sound("default")
				.badge(getBadgeCount()).conversationId(getConversationId()).build();
	}

	@Override
	protected String createBrowserPayload() {
		// FIXME (KER-296): new Date no deberian instanciarse aca. Factory de notificaciones ?
		MessagePayload messagePayload = MessagePayload.builder().withDate(new Date()).withSenderId(getNotificationRemitter().getSenderId())
				.withMessageId(getMessageId()).withText(getChatMessage()).build();
		return new BrowserPayloadBuilder().withAlertType(NEW_MESSAGE).withType(BrowserNotificationType.MESSAGE).withData(messagePayload).build();
	}
}