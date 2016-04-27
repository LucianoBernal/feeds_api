package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.dto.ConversationMessageDisplayDTO;
import com.etermax.conversations.dto.TextMessageCreationDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.ConversationMessagesResource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversationMessagesResourceTest {

	@Test
	public void testSaveConversationMessage() throws Exception {
		//GIVEN
		TextMessageCreationDTO messageDTO = mock(TextMessageCreationDTO.class);
		when(messageDTO.accept(any(), anyString())).thenReturn(mock(ConversationMessageDisplayDTO.class));
		MessageAdapter adapter = mock(MessageAdapter.class);

		//WHEN
		ConversationMessagesResource resource = new ConversationMessagesResource(adapter);
		ConversationDataDTO displayDTO = resource.saveMessage("1", messageDTO);

		//THEN
		assertThat(displayDTO).isNotNull();
	}

	@Test
	public void testSaveInvalidConversationMessages() throws Exception {
		//GIVEN
		TextMessageCreationDTO messageDTO = mock(TextMessageCreationDTO.class);
		when(messageDTO.accept(any(), anyString())).thenThrow(mock(ClientException.class));
		MessageAdapter adapter = mock(MessageAdapter.class);

		//WHEN
		ConversationMessagesResource resource = new ConversationMessagesResource(adapter);

		//THEN
		assertThatThrownBy(() -> resource.saveMessage("1", messageDTO)).isInstanceOf(ClientException.class);
	}

}
