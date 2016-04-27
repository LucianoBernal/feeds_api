package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.ConversationCreationDTO;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.error.TooFewUsersException;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConversationCreationDTOTest {

	@Test
	public void testNullUsers() {
		//Given
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = conversationCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(TooFewUsersException.class);
	}

	@Test
	public void testEmptyUsers() {
		//Given
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTOWithNoUsers();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = conversationCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(TooFewUsersException.class);
	}

	@Test
	public void testInvalidUser() {
		//Given
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTOWithInvalidUser();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = conversationCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(InvalidUserException.class);
	}

	@Test
	public void testValidDTO() throws InvalidDTOException {
		//Given
		ConversationCreationDTO conversationCreationDTO = givenAValidConversationCreationDTO();

		//When
		conversationCreationDTO.validate();

		//Then
		assertThat(conversationCreationDTO.getUsers()).containsExactly(1l, 2l);

	}

	private ConversationCreationDTO givenAConversationCreationDTOWithInvalidUser() {
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		conversationCreationDTO.setUsers(Sets.newHashSet(1l, 0l));
		return conversationCreationDTO;
	}

	private ConversationCreationDTO givenAValidConversationCreationDTO() {
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		Set<Long> users = Sets.newHashSet(1l, 2l);
		conversationCreationDTO.setUsers(users);
		return conversationCreationDTO;
	}

	private ConversationCreationDTO givenAConversationCreationDTOWithNoUsers() {
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		conversationCreationDTO.setUsers(new HashSet<>());
		return conversationCreationDTO;
	}

	private ConversationCreationDTO givenAConversationCreationDTO() {
		return new ConversationCreationDTO();
	}

}
