package com.etermax.conversations.test.unit.model;

import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.factory.ConversationMessageFactory;
import com.etermax.conversations.factory.UserFactory;
import com.etermax.conversations.model.User;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MessageTest {

	@Test
	public void testEmptyMessage() throws InvalidUserException {
		//Given
		ConversationMessageFactory conversationMessageFactory = givenAMessageFactory();
		UserFactory userFactory = givenAUserFactory();
		User userOne = givenAUser(userFactory);
		User userTwo = givenAnotherUser(userFactory);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationMessageFactory
				.createTextConversationMessage("", userOne, "1", "A2", false);

		//Then

		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidMessageException.class);

	}

	private User givenAUser(UserFactory userFactory) throws InvalidUserException {
		return userFactory.createUser(1l);
	}

	private User givenAnotherUser(UserFactory userFactory) throws InvalidUserException {
		return userFactory.createUser(2l);
	}

	private UserFactory givenAUserFactory() {
		return new UserFactory();
	}

	private ConversationMessageFactory givenAMessageFactory() {
		return new ConversationMessageFactory();
	}

}
