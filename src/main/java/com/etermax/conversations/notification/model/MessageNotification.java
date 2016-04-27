package com.etermax.conversations.notification.model;

import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.TextFormatter;
import com.etermax.conversations.model.User;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.stream.Collectors;

public class MessageNotification implements Notification {

	private Conversation conversation;
	private ConversationMessage message;

	public MessageNotification(Conversation conversation, ConversationMessage message) {
		this.conversation = conversation;
		this.message = message;
	}

	@Override
	public NotificationResult acceptSender(NotificationSender notificationSender, TextFormatter formatter) {
		return notificationSender.send(getSender(), getReceivers(), getText(formatter), message.getApplication(), message.getId(), message.getConversationId());
	}

	public String getText(TextFormatter formatter) {
		return message.acceptFormatter(formatter);
	}

	private User getSender() {
		return message.getSender();
	}

	public List<User> getReceivers() {
		return conversation.getUsers()
						   .stream()
						   .filter(user -> !user.getId().equals(message.getSender().getId()))
						   .collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
