package com.etermax.conversations.test.unit.model;

import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.factory.UserFactory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {

	@Test
	public void testInvalidMessage() {
		//Given
		UserFactory userFactory = givenAUserFactory();

		//When Then
		assertThatThrownBy(() -> userFactory.createUser(0l)).isInstanceOf(InvalidUserException.class);

	}

	private UserFactory givenAUserFactory() {
		return new UserFactory();
	}
}
