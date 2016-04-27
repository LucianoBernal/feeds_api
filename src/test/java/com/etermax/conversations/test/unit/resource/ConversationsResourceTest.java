package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationCreationDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.ConversationsResource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversationsResourceTest {

	@Test
	public void testSaveValidConversation() throws Exception {
		//GIVEN
		ConversationAdapter adapter = mock(ConversationAdapter.class);
		ConversationCreationDTO creationDTO = mock(ConversationCreationDTO.class);
		when(adapter.saveConversation(creationDTO)).thenReturn(mock(ConversationDisplayDTO.class));

		//WHEN
		ConversationsResource resource = new ConversationsResource(adapter);
		ConversationDisplayDTO displayDTO = resource.saveConversation(creationDTO);

		//THEN
		assertThat(displayDTO).isNotNull();
	}

	@Test
	public void testSaveInvalidConversation() throws Exception {
		//GIVEN
		ConversationAdapter adapter = mock(ConversationAdapter.class);
		ConversationCreationDTO creationDTO = mock(ConversationCreationDTO.class);
		when(adapter.saveConversation(creationDTO)).thenThrow(mock(ClientException.class));

		//WHEN
		ConversationsResource resource = new ConversationsResource(adapter);

		//THEN
		assertThatThrownBy(() -> resource.saveConversation(creationDTO)).isInstanceOf(ClientException.class);
	}

}
