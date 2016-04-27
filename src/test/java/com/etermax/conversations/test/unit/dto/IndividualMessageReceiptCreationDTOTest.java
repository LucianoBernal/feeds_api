package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.IndividualMessageReceiptCreationDTO;
import com.etermax.conversations.error.InvalidDTOException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IndividualMessageReceiptCreationDTOTest {

	@Test
	public void validDTOTest() {
		//Given
		IndividualMessageReceiptCreationDTO individualMessageReceiptCreationDTO = new IndividualMessageReceiptCreationDTO();

		//When
		individualMessageReceiptCreationDTO.setUserId(1l);
		individualMessageReceiptCreationDTO.setType("received");

		//Then
		assertThat(individualMessageReceiptCreationDTO.getType()).isEqualTo("received");
		assertThat(individualMessageReceiptCreationDTO.getUserId()).isEqualTo(1l);
	}

	@Test
	public void invalidUserTest() throws InvalidDTOException {
		//Given
		IndividualMessageReceiptCreationDTO individualMessageReceiptCreationDTO = new IndividualMessageReceiptCreationDTO();
		individualMessageReceiptCreationDTO.setType("received");

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = individualMessageReceiptCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
	}

	@Test
	public void invalidTypeTest() {
		//Given
		IndividualMessageReceiptCreationDTO individualMessageReceiptCreationDTO = new IndividualMessageReceiptCreationDTO();
		individualMessageReceiptCreationDTO.setUserId(1l);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = individualMessageReceiptCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
	}
}
