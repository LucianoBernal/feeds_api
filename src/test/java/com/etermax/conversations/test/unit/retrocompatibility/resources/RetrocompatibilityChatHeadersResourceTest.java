package com.etermax.conversations.test.unit.retrocompatibility.resources;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityConversationAdapter;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityUserAdapter;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityChatHeadersDTO;
import com.etermax.conversations.retrocompatibility.resource.RetrocompatibilityChatHeadersResource;
import org.junit.Test;
import retrocompatibility.dto.RetrocompatibilityUserDTO;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RetrocompatibilityChatHeadersResourceTest {

//	@Test
//	public void getChatHeadersTest() {
//		//Given
//		ConversationAdapter conversationAdapter = mock(ConversationAdapter.class);
//		when(conversationAdapter.getUserConversations(anyLong())).thenReturn(
//				Collections.singletonList(mock(ConversationDisplayDTO.class)));
//		MessageAdapter messageAdapter = mock(MessageAdapter.class);
//		when(messageAdapter.getLastMessages(anyLong(), anyList(), anyString())).thenReturn(
//				Collections.singletonList(mock(AddressedMessageDisplayDTO.class)));
//		RetrocompatibilityUserAdapter retrocompatibilityUserAdapter = mock(RetrocompatibilityUserAdapter.class);
//		when(retrocompatibilityUserAdapter.getReceivers(anyLong(), anyList())).thenReturn(
//				Collections.singletonList(mock(RetrocompatibilityUserDTO.class)));
//
//		RetrocompatibilityConversationAdapter adapter = mock(RetrocompatibilityConversationAdapter.class);
//		when(adapter.getChatHeaders(anyLong(), anyString())).thenReturn(mock(RetrocompatibilityChatHeadersDTO.class));
//
//		RetrocompatibilityChatHeadersResource retrocompatibilityChatHeadersResource = new
//				RetrocompatibilityChatHeadersResource(
//				adapter);
//		//When
//		RetrocompatibilityChatHeadersDTO chatHeaders = retrocompatibilityChatHeadersResource.getChatHeaders(1l, "A2");
//
//		//Then
//		assertThat(chatHeaders).isNotNull();
//	}
}
