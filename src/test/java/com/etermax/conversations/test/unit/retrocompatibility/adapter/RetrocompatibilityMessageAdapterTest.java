package com.etermax.conversations.test.unit.retrocompatibility.adapter;

import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityMessageAdapter;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityConversationMessageDeletionDTO;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.MessageService;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RetrocompatibilityMessageAdapterTest {

	@Test
	public void deleteMessageTestInvalidDTO() throws InvalidDTOException {
		//Given
		ConversationService conversationService = mock(ConversationService.class);
		MessageService messageService = mock(MessageService.class);

		RetrocompatibilityMessageAdapter adapter = new RetrocompatibilityMessageAdapter(conversationService,
				messageService);
		RetrocompatibilityConversationMessageDeletionDTO deletionDTO = mock(
				RetrocompatibilityConversationMessageDeletionDTO.class);
		doThrow(InvalidDTOException.class).when(deletionDTO).validate();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> adapter.deleteMessage(deletionDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(InvalidDTOException.class);
	}

	@Test
	public void deleteMessageTestConversationNotFound() throws InvalidDTOException {
		//Given
		ConversationService conversationService = mock(ConversationService.class);
		MessageService messageService = mock(MessageService.class);

		RetrocompatibilityMessageAdapter adapter = new RetrocompatibilityMessageAdapter(conversationService,
				messageService);
		when(conversationService.getConversationWithUsers(any())).thenThrow(ConversationNotFoundException.class);
		RetrocompatibilityConversationMessageDeletionDTO deletionDTO = mock(
				RetrocompatibilityConversationMessageDeletionDTO.class);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> adapter.deleteMessage(deletionDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(ConversationNotFoundException.class);
	}

}
