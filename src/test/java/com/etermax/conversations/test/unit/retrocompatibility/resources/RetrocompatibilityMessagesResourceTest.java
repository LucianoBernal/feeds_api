package com.etermax.conversations.test.unit.retrocompatibility.resources;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.metrics.NoneNotificationMetricsPublisher;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityConversationAdapter;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityUserAdapter;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityConversationDTO;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityMessageDTO;
import com.etermax.conversations.retrocompatibility.resource.RetrocompatibilityMessagesResource;
import com.google.common.collect.Maps;
import dto.UserDTO;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RetrocompatibilityMessagesResourceTest {
	@Test
	public void getMessagesTest() {
		//Given
		RetrocompatibilityUserAdapter userAdapter = mock(RetrocompatibilityUserAdapter.class);
		UserDTO userDTO = mock(UserDTO.class);
		HashMap<String, Object> extensions = Maps.newHashMap();
		extensions.put("social_interactions", Maps.newHashMap());
		when(userDTO.getExtensions()).thenReturn(extensions);
		when(userAdapter.getUser(anyList())).thenReturn(userDTO);
		MessageAdapter messageAdapter = mock(MessageAdapter.class);
		when(messageAdapter.getRetrocompatibilityUserMessages(anyList(), any(), anyString()))
				.thenReturn(Collections.singletonList(mock(AddressedMessageDisplayDTO.class)));

		RetrocompatibilityConversationAdapter adapter = mock(RetrocompatibilityConversationAdapter.class);
		when(adapter.getMessages(anyList(), anyString())).thenReturn(mock(RetrocompatibilityConversationDTO.class));

		RetrocompatibilityMessagesResource retrocompatibilityMessagesResource = new RetrocompatibilityMessagesResource(
				adapter, new NoneNotificationMetricsPublisher());
		//When
		RetrocompatibilityConversationDTO messages = retrocompatibilityMessagesResource.getMessages("1,2", "A2");

		//Then
		assertThat(messages).isNotNull();
	}

	@Test
	public void getMessagesClientExceptionTest() {
		//Given
		RetrocompatibilityUserAdapter userAdapter = mock(RetrocompatibilityUserAdapter.class);
		MessageAdapter messageAdapter = mock(MessageAdapter.class);
		when(messageAdapter.getRetrocompatibilityUserMessages(anyList(), any(), anyString()))
				.thenThrow(mock(ClientException.class));

		RetrocompatibilityConversationAdapter adapter = mock(RetrocompatibilityConversationAdapter.class);
		when(adapter.getMessages(anyList(), anyString())).thenThrow(mock(ClientException.class));

		RetrocompatibilityMessagesResource retrocompatibilityMessagesResource = new RetrocompatibilityMessagesResource(
				adapter, new NoneNotificationMetricsPublisher());
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> retrocompatibilityMessagesResource
				.getMessages("1", "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
	}

	@Test
	public void saveMessageTest() {
		//Given
		RetrocompatibilityUserAdapter userAdapter = mock(RetrocompatibilityUserAdapter.class);
		MessageAdapter messageAdapter = mock(MessageAdapter.class);
		when(messageAdapter.saveMessage(any())).thenReturn(mock(AddressedMessageDisplayDTO.class));

		RetrocompatibilityConversationAdapter adapter = mock(RetrocompatibilityConversationAdapter.class);
		when(adapter.saveMessage(any())).thenReturn(mock(RetrocompatibilityMessageDTO.class));

		RetrocompatibilityMessagesResource retrocompatibilityMessagesResource = new RetrocompatibilityMessagesResource(
				adapter, new NoneNotificationMetricsPublisher());

		//When
		RetrocompatibilityMessageDTO retrocompatibilityMessageDTO = retrocompatibilityMessagesResource
				.saveMessage(mock(AddressedMessageCreationDTO.class));

		//Then
		assertThat(retrocompatibilityMessageDTO).isNotNull();
	}

	@Test
	public void saveMessageClientExceptionTest() {
		//Given
		RetrocompatibilityUserAdapter userAdapter = mock(RetrocompatibilityUserAdapter.class);
		MessageAdapter messageAdapter = mock(MessageAdapter.class);
		when(messageAdapter.saveMessage(any())).thenThrow(mock(ClientException.class));

		RetrocompatibilityConversationAdapter adapter = mock(RetrocompatibilityConversationAdapter.class);
		when(adapter.saveMessage(any())).thenThrow(mock(ClientException.class));

		RetrocompatibilityMessagesResource retrocompatibilityMessagesResource = new RetrocompatibilityMessagesResource(
				adapter, new NoneNotificationMetricsPublisher());
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> retrocompatibilityMessagesResource
				.saveMessage(mock(AddressedMessageCreationDTO.class));

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
	}

}
