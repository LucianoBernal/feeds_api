package com.etermax.conversations.factory;

import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.ConversationTextMessage;
import com.etermax.conversations.model.User;

import java.util.Date;

public class ConversationMessageFactory {

	public ConversationMessage createTextConversationMessage(String text, User sender, String conversationId,
			String application, Boolean ignored) throws InvalidMessageException {
		Date date = new Date();
		return new ConversationTextMessage(sender, conversationId, date, text, application, ignored);

	}
}
