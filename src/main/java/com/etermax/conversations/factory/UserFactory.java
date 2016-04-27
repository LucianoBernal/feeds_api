package com.etermax.conversations.factory;

import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.error.InvalidUserIdException;
import com.etermax.conversations.model.User;

public class UserFactory {
	public User createUser(Long userId) throws InvalidUserException {
		if (userId == null) {
			throw new InvalidUserException(new InvalidUserIdException());
		}
		return new User(userId);
	}
}
