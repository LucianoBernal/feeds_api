package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.resource.MessagesResource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessagesResourceTest {

	@Test
	public void testValidMessage() throws Exception {
		//GIVEN
		AddressedMessageCreationDTO dto = mock(AddressedMessageCreationDTO.class);
		MessageAdapter adapter = mock(MessageAdapter.class);
		when(adapter.saveMessage(dto)).thenReturn(mock(AddressedMessageDisplayDTO.class));

		//WHEN
		MessagesResource resource = new MessagesResource(adapter);
		AddressedMessageDisplayDTO displayDTO = resource.saveMessage(dto);

		//THEN
		assertThat(displayDTO).isNotNull();
	}

	@Test
	public void testInvalidMessage() throws Exception {
		//GIVEN
		AddressedMessageCreationDTO dto = mock(AddressedMessageCreationDTO.class);
		MessageAdapter adapter = mock(MessageAdapter.class);
		when(adapter.saveMessage(dto)).thenThrow(mock(ClientException.class));

		//WHEN
		MessagesResource resource = new MessagesResource(adapter);

		//THEN
		assertThatThrownBy(() -> resource.saveMessage(dto)).isInstanceOf(ClientException.class);
	}

}
