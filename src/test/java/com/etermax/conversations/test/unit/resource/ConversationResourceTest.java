package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.ConversationResource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversationResourceTest {

	@Test
	public void testGetConversationMessages() throws Exception {
		//GIVEN
		ConversationAdapter adapter = mock(ConversationAdapter.class);
		when(adapter.getConversation(anyString())).thenReturn(mock(ConversationDisplayDTO.class));

		//WHEN
		ConversationResource resource = new ConversationResource(adapter);
		ConversationDisplayDTO displayDTO = resource.getConversation("1");

		//THEN
		assertThat(displayDTO).isNotNull();
	}

	@Test
	public void testGetInvalidConversationMessages() throws Exception {
		//GIVEN
		ConversationAdapter adapter = mock(ConversationAdapter.class);
		when(adapter.getConversation(anyString())).thenThrow(mock(ClientException.class));

		//WHEN
		ConversationResource resource = new ConversationResource(adapter);

		//THEN
		assertThatThrownBy(() -> resource.getConversation("1")).isInstanceOf(ClientException.class);
	}

}
