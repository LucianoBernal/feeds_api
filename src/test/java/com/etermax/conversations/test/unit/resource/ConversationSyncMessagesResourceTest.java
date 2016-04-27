package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.HistoryDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.HistoryResource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversationSyncMessagesResourceTest {

	@Test
	public void testGetValidConversationMessages() throws Exception {
		//GIVEN
		SynchronizationAdapter adapter = mock(SynchronizationAdapter.class);
		when(adapter.getConversationHistory(any(), any(), any(), any(), anyString()))
				.thenReturn(mock(HistoryDTO.class));

		//WHEN
		HistoryResource resource = new HistoryResource(adapter);
		HistoryDTO displayDTO = resource.getMessagesFromId(1L, "1", 1L, 1L, "A2");

		//THEN
		assertThat(displayDTO).isNotNull();
	}

	@Test
	public void testGetInvalidConversationMessages() throws Exception {
		//GIVEN
		SynchronizationAdapter adapter = mock(SynchronizationAdapter.class);
		when(adapter.getConversationHistory(any(), any(), any(), any(), anyString())).thenThrow(mock(ClientException.class));

		//WHEN
		HistoryResource resource = new HistoryResource(adapter);

		//THEN
		assertThatThrownBy(() -> resource.getMessagesFromId(1L, "1", 1L, 1L, "A2")).isInstanceOf(ClientException.class);
	}

}
