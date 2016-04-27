package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.SyncDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.SyncResource;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SyncMessagesResourceTest {

	@Test
	public void syncResourceTest() {
		//Given
		SynchronizationAdapter synchronizationAdapter = mock(SynchronizationAdapter.class);
		when(synchronizationAdapter.getConversationSync(anyLong(), anyString(), anyString())).thenReturn(mock(List.class));

		//When
		SyncResource syncMessagesResource = new SyncResource(synchronizationAdapter);
		List<SyncDTO> userMessages = syncMessagesResource.getConversationSync(1l, "", "A2");

		//Then
		assertThat(userMessages).isNotNull();
	}

	@Test
	public void invalidSyncResourceTest() {
		//Given
		SynchronizationAdapter synchronizationAdapter = mock(SynchronizationAdapter.class);
		when(synchronizationAdapter.getConversationSync(anyLong(), anyString(), anyString())).thenThrow(mock(ClientException.class));

		//When
		SyncResource syncMessagesResource = new SyncResource(synchronizationAdapter);
		ThrowableAssert.ThrowingCallable throwingCallable = () -> syncMessagesResource.getConversationSync(1l, "", "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
	}
}
