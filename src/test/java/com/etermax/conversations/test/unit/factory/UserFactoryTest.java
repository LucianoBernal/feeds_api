package com.etermax.conversations.test.unit.factory;

import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.factory.UserFactory;
import com.etermax.conversations.model.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserFactoryTest {

	@Test
	public void testValidUser() throws Exception {
		//GIVEN
		UserFactory factory = new UserFactory();

		//WHEN
		User user = factory.createUser(1L);

		//THEN
		assertThat(user).isNotNull();
		assertThat(user.getId()).isEqualTo(1L);
	}

	@Test
	public void testInvalidUser() throws Exception {
		//GIVEN
		UserFactory factory = new UserFactory();

		//WHEN AND THEN
		assertThatThrownBy(() -> factory.createUser(null)).isInstanceOf(InvalidUserException.class);
	}

}
