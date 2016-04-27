package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.MessageReceiptDTO;
import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.model.MessageReceipt;
import com.etermax.conversations.model.ReceivedType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageReceiptDTOTest {

	@Test
	public void messageReceiptDTOTest() throws ModelException {
		//Given
		List<IndividualMessageReceipt> receipts = new ArrayList<>();
		IndividualMessageReceipt individualMessageReceipt = mock(IndividualMessageReceipt.class);
		when(individualMessageReceipt.getType()).thenReturn(new ReceivedType());
		when(individualMessageReceipt.getDate()).thenReturn(new Date(1));
		when(individualMessageReceipt.getUser()).thenReturn(1l);
		receipts.add(individualMessageReceipt);
		MessageReceipt messageReceipt = new MessageReceipt("1", receipts, "1", "A2");

		//When
		MessageReceiptDTO messageReceiptDTO = new MessageReceiptDTO(messageReceipt);

		//Then
		assertThat(messageReceiptDTO.getMessageId()).isEqualTo("1");
		assertThat(messageReceiptDTO.getReceipts()).extracting("type").containsExactly("received");
		assertThat(messageReceiptDTO.getReceipts()).extracting("timestamp").containsExactly(1l);
		assertThat(messageReceiptDTO.getReceipts()).extracting("userId").containsExactly(1l);
	}
}
