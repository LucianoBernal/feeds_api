package com.etermax.conversations.factory;

import com.etermax.conversations.error.InvalidConversation;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.User;

import java.util.Set;

public class ConversationFactory {

	public Conversation createConversation(Set<User> users) throws InvalidConversation {
		return new Conversation(users);
	}

}
