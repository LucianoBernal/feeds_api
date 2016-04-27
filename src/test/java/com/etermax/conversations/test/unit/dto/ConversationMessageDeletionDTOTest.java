package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.error.EmptyUserException;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.error.InvalidUserIdException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConversationMessageDeletionDTOTest {

	@Test
	public void testEmptyUserDTO() {
		//Given
		ConversationMessageDeletionDTO conversationMessageDeletionDTO = givenAMessageConversationDeletionDTO();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = conversationMessageDeletionDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasRootCauseInstanceOf(EmptyUserException.class);

	}

	@Test
	public void testInvalidUser() {
		//Given
		ConversationMessageDeletionDTO conversationMessageDeletionDTO = givenAMessageConversationDeletionDTO();
		conversationMessageDeletionDTO.setUserId(0l);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = conversationMessageDeletionDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasRootCauseInstanceOf(InvalidUserIdException.class);
	}

	@Test
	public void testValidDeletionDTOTest() throws InvalidDTOException {
		//Given
		ConversationMessageDeletionDTO conversationMessageDeletionDTO = givenAMessageConversationDeletionDTO();

		//When
		conversationMessageDeletionDTO.setUserId(1l);

		//Then
		assertThat(conversationMessageDeletionDTO.getUserId()).isEqualTo(1l);
	}

	private ConversationMessageDeletionDTO givenAMessageConversationDeletionDTO() {
		return new ConversationMessageDeletionDTO();
	}

}
