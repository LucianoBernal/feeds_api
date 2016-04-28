package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.ConversationMessageDeletionResource;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

public class ConversationMessagesDeletionResourceTest {

	@Test
	public void testDeleteConversationMessage() throws Exception {
		//GIVEN
		MessageAdapter adapter = mock(MessageAdapter.class);
		ConversationMessageDeletionDTO deletionDTO = mock(ConversationMessageDeletionDTO.class);
		Mockito.doNothing().when(adapter).deleteMessage(anyString(), anyString(), any());

		//WHEN
		ConversationMessageDeletionResource resource = new ConversationMessageDeletionResource(adapter);
		resource.deleteMessage("1", "1", deletionDTO);
	}

	@Test
	public void testDeleteInvalidConversationMessage() throws Exception {
		//GIVEN
		MessageAdapter adapter = mock(MessageAdapter.class);
		ConversationMessageDeletionDTO deletionDTO = mock(ConversationMessageDeletionDTO.class);
		Mockito.doThrow(mock(ClientException.class)).when(adapter).deleteMessage(any(), anyString(), any());

		//WHEN
		ConversationMessageDeletionResource resource = new ConversationMessageDeletionResource(adapter);

		assertThatThrownBy(() -> resource.deleteMessage("1", "1", deletionDTO)).isInstanceOf(ClientException.class);
	}

}
