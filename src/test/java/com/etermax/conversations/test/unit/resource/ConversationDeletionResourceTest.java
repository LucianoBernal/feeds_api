package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.ConversationDeletionResource;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

public class ConversationDeletionResourceTest {

	@Test
	public void deleteConversationTest() {
		//Given
		ConversationAdapter conversationAdapter = mock(ConversationAdapter.class);
		MessageAdapter messageAdapter = mock(MessageAdapter.class);
		Mockito.doNothing().when(messageAdapter).deleteMessage(anyString(), anyString(), any());
		ConversationMessageDeletionDTO conversationDeletionDTO = mock(ConversationMessageDeletionDTO.class);
		//When
		ConversationDeletionResource conversationDeletionResource = new ConversationDeletionResource(conversationAdapter);
		conversationDeletionResource.deleteConversation("1", conversationDeletionDTO);

		//Then

	}

	@Test
	public void invalidConversationDTOTest() {
		//Given
		ConversationAdapter adapter = mock(ConversationAdapter.class);
		Mockito.doThrow(mock(ClientException.class)).when(adapter).deleteConversation(anyString(), any());
		ConversationMessageDeletionDTO conversationDeletionDTO = mock(ConversationMessageDeletionDTO.class);

		//When
		ConversationDeletionResource conversationDeletionResource = new ConversationDeletionResource(adapter);
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationDeletionResource
				.deleteConversation("1", conversationDeletionDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);

	}
}
