package com.etermax.conversations.test.unit.adapter;

import com.etermax.conversations.adapter.ReceiptAdapter;
import com.etermax.conversations.adapter.impl.ReceiptAdapterImpl;
import com.etermax.conversations.dto.IndividualMessageReceiptCreationDTO;
import com.etermax.conversations.dto.IndividualMessageReceiptDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.error.SaveReceiptException;
import com.etermax.conversations.factory.IndividualMessageReceiptFactory;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.model.ReceivedType;
import com.etermax.conversations.service.ReceiptService;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReceiptAdapterTest {

	@Test
	public void saveReceiptMessage() throws SaveReceiptException, InvalidDTOException {
		//Given
		ReceiptService receiptService = mock(ReceiptService.class);
		IndividualMessageReceiptFactory individualMessageReceiptFactory = mock(IndividualMessageReceiptFactory.class);
		IndividualMessageReceipt individualMessageReceipt = givenAnIndividualMessageReceipt();
		when(receiptService.saveReceiptInMessage(anyString(), anyString(), any(IndividualMessageReceipt.class)))
				.thenReturn(individualMessageReceipt);
		ReceiptAdapter receiptAdapter = new ReceiptAdapterImpl(receiptService, individualMessageReceiptFactory);

		IndividualMessageReceiptCreationDTO dto = givenAIndividualMessageReceiptCreationDTO();

		//When
		IndividualMessageReceiptDisplayDTO individualMessageReceiptDisplayDTO = receiptAdapter
				.saveReceiptInMessage("1", "1", dto);
		//Then
		assertThat(individualMessageReceiptDisplayDTO).isNotNull();
		assertThat(individualMessageReceiptDisplayDTO).isInstanceOf(IndividualMessageReceiptDisplayDTO.class);
	}

	@Test
	public void invalidDtoSaveReceiptMessage() throws SaveReceiptException, InvalidDTOException {
		//Given
		IndividualMessageReceiptFactory individualMessageReceiptFactory = mock(IndividualMessageReceiptFactory.class);
		ReceiptService receiptService = mock(ReceiptService.class);
		when(receiptService.saveReceiptInMessage(anyString(), anyString(), any(IndividualMessageReceipt.class)))
				.thenReturn(mock(IndividualMessageReceipt.class));
		ReceiptAdapter receiptAdapter = new ReceiptAdapterImpl(receiptService, individualMessageReceiptFactory);
		IndividualMessageReceiptCreationDTO dto = new IndividualMessageReceiptCreationDTO();
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> receiptAdapter.saveReceiptInMessage("1", "1", dto);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(InvalidDTOException.class);
	}

	@Test
	public void invalidSaveReceiptMessage() throws SaveReceiptException {
		//Given
		IndividualMessageReceiptFactory individualMessageReceiptFactory = mock(IndividualMessageReceiptFactory.class);
		ReceiptService receiptService = mock(ReceiptService.class);
		when(receiptService.saveReceiptInMessage(anyString(), anyString(), any(IndividualMessageReceipt.class)))
				.thenThrow(SaveReceiptException.class);
		ReceiptAdapter receiptAdapter = new ReceiptAdapterImpl(receiptService, individualMessageReceiptFactory);
		IndividualMessageReceiptCreationDTO dto = givenAIndividualMessageReceiptCreationDTO();
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> receiptAdapter.saveReceiptInMessage("1", "1", dto);
		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(SaveReceiptException.class);
	}

	private IndividualMessageReceipt givenAnIndividualMessageReceipt() {
		IndividualMessageReceipt individualMessageReceipt = mock(IndividualMessageReceipt.class);
		when(individualMessageReceipt.getType()).thenReturn(new ReceivedType());
		when(individualMessageReceipt.getDate()).thenReturn(new Date(1l));
		when(individualMessageReceipt.getUser()).thenReturn(1l);
		return individualMessageReceipt;
	}

	private IndividualMessageReceiptCreationDTO givenAIndividualMessageReceiptCreationDTO() {
		IndividualMessageReceiptCreationDTO dto = mock(IndividualMessageReceiptCreationDTO.class);
		when(dto.getType()).thenReturn("received");
		when(dto.getUserId()).thenReturn(1l);
		return dto;
	}
}
