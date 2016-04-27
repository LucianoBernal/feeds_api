package com.etermax.conversations.test.unit.adapter;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.impl.ConversationAdapterImpl;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.GetConversationException;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.service.ConversationService;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversationAdapterTest {

	@Test
	public void getConversationTest() throws GetConversationException {
		//GIVEN
		ConversationService service = mock(ConversationService.class);
		when(service.getConversation(anyString())).thenReturn(mock(Conversation.class));

		//WHEN
		ConversationAdapter adapter = new ConversationAdapterImpl(service);

		//THEN
		assertThat(adapter.getConversation("1")).isNotNull();

	}

	@Test
	public void getInvalidConversationTest() throws GetConversationException {
		//GIVEN
		ConversationAdapter adapter = mock(ConversationAdapter.class);

		//WHEN
		when(adapter.getConversation(anyString())).thenThrow(mock(ClientException.class));

		//THEN
		assertThatThrownBy(() -> adapter.getConversation("1")).isInstanceOf(ClientException.class);

	}

	@Test
	public void getConversationFromDtoTest() throws GetConversationException {
		//GIVEN
		ConversationService service = mock(ConversationService.class);
		when(service.getConversation(anyString())).thenReturn(mock(Conversation.class));
		ConversationAdapter adapter = new ConversationAdapterImpl(service);

		//WHEN
		ConversationDisplayDTO dto = adapter.getConversation("1");

		//THEN
		assertThat(dto).isNotNull();

	}

	@Test
	public void getUserConversationsTest() {
		//GIVEN
		List<Conversation> conversations = givenAConversationList();
		ConversationService service = mock(ConversationService.class);
		when(service.getUserConversations(anyLong())).thenReturn(conversations);

		//WHEN
		ConversationAdapter adapter = new ConversationAdapterImpl(service);
		List<ConversationDisplayDTO> userConversations = adapter.getUserConversations(1L);

		//THEN
		assertThat(userConversations).isNotEmpty();

	}

	private List<Conversation> givenAConversationList() {
		Conversation conversation = mock(Conversation.class);
		return Arrays.asList(conversation);
	}



}
