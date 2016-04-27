package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.IndividualMessageReceiptDisplayDTO;
import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.model.ReceivedType;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndividualMessageReceiptDisplayDTOTest {

	@Test
	public void individualMessageReceiptDTOTest() throws ModelException {
		//Given
		IndividualMessageReceipt individualMessageReceipt = mock(IndividualMessageReceipt.class);
		when(individualMessageReceipt.getType()).thenReturn(new ReceivedType());
		when(individualMessageReceipt.getDate()).thenReturn(new Date(1));
		when(individualMessageReceipt.getUser()).thenReturn(1l);

		//When
		IndividualMessageReceiptDisplayDTO individualMessageReceiptDisplayDTO = new IndividualMessageReceiptDisplayDTO(
				individualMessageReceipt);

		//Then
		assertThat(individualMessageReceiptDisplayDTO.getTimestamp()).isEqualTo(1l);
		assertThat(individualMessageReceiptDisplayDTO.getUserId()).isEqualTo(1l);
		assertThat(individualMessageReceiptDisplayDTO.getType()).isEqualTo("received");
	}

}
