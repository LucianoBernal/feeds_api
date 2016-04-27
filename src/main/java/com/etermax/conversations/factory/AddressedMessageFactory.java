package com.etermax.conversations.factory;

import com.etermax.conversations.model.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AddressedMessageFactory {
	public AddressedMessage createAddressedMessage(String text, User sender, User receiver, String application,
			Boolean blocked) {
		Date date = new Date();
		return new AddressedMessage(text, sender, receiver, date, application, blocked);
	}

	public AddressedMessage createAddressedMessage(ConversationMessage conversationMessage, Conversation conversation) {
		TextFormatter textFormatter = new RetrocompatibilityTextFormatter();
		String message = conversationMessage.acceptFormatter(textFormatter);
		User sender = conversationMessage.getSender();
		String application = conversationMessage.getApplication();
		Boolean blocked = conversationMessage.getIgnored();
		List<User> users = conversation.getUsers().stream().filter(user -> !user.equals(sender))
				.collect(Collectors.toList());

		User receiver = users.get(0); //FIXME ver el caso de conversaciones de mas de dos
		Date date = conversationMessage.getDate();
		String id = conversationMessage.getId();
		AddressedMessage addressedMessage = new AddressedMessage(message, sender, receiver, date, application, blocked);
		addressedMessage.setId(id);
		return addressedMessage;
	}
}
