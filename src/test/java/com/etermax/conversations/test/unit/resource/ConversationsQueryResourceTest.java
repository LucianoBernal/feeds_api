package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.ConversationsQueryResource;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversationsQueryResourceTest {

	@Test
	public void testGetUserConversations() throws Exception {
		//GIVEN
		ConversationAdapter adapter = mock(ConversationAdapter.class);
		when(adapter.getUserConversations(any())).thenReturn(mock(List.class));

		//WHEN
		ConversationsQueryResource resource = new ConversationsQueryResource(adapter);
		List<ConversationDisplayDTO> displayDTO = resource.getConversations(1L);

		//THEN
		assertThat(displayDTO).isNotNull();
	}

	@Test
	public void testGetInvalidUserConversations() throws Exception {
		//GIVEN
		ConversationAdapter adapter = mock(ConversationAdapter.class);
		when(adapter.getUserConversations(any())).thenThrow(mock(ClientException.class));

		//WHEN
		ConversationsQueryResource resource = new ConversationsQueryResource(adapter);

		//THEN
		assertThatThrownBy(() -> resource.getConversations(1L)).isInstanceOf(ClientException.class);
	}

}
