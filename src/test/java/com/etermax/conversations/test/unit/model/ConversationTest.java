package com.etermax.conversations.test.unit.model;

import com.etermax.conversations.error.InvalidConversation;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.factory.ConversationFactory;
import com.etermax.conversations.factory.UserFactory;
import com.etermax.conversations.model.User;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConversationTest {
	@Test
	public void invalidConversation() throws InvalidUserException {
		//Given
		ConversationFactory conversationFactory = givenAConversationFactory();
		UserFactory userFactory = givenAUserFactory();
		User userOne = userFactory.createUser(1l);
		Set<User> users = Sets.newHashSet(userOne);
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationFactory.createConversation(users);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidConversation.class);
	}

	private UserFactory givenAUserFactory() {
		return new UserFactory();
	}

	private ConversationFactory givenAConversationFactory() {
		return new ConversationFactory();
	}

}
