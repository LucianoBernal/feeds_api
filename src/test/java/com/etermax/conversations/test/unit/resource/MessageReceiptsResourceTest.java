package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.adapter.ReceiptAdapter;
import com.etermax.conversations.dto.IndividualMessageReceiptCreationDTO;
import com.etermax.conversations.dto.IndividualMessageReceiptDisplayDTO;
import com.etermax.conversations.resource.MessageReceiptsResource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageReceiptsResourceTest {

	String conversationId = "1";
	String messageId = "1";

	@Test
	public void testValidSavedReceipt() {
		//GIVEN
		IndividualMessageReceiptCreationDTO dto = new IndividualMessageReceiptCreationDTO();
		ReceiptAdapter receiptAdapter = mock(ReceiptAdapter.class);
		when(receiptAdapter.saveReceiptInMessage(conversationId, messageId, dto))
				.thenReturn(mock(IndividualMessageReceiptDisplayDTO.class));

		//WHEN
		MessageReceiptsResource resource = new MessageReceiptsResource(receiptAdapter);

		//THEN
		assertThat(resource.saveReceipt(conversationId, messageId, dto))
				.isInstanceOf(IndividualMessageReceiptDisplayDTO.class);
	}

}
