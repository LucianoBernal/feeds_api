package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.error.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AddressedMessageCreationDTOTest {

	@Test
	public void testNullMessage() {
		//Given
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTOWithNullMessage();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = addressedMessageCreationDTO::validate;
		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(EmptyMessageTextException.class);
	}

	@Test
	public void testNullSenderId() {
		//Given
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTOWithNullSender();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = addressedMessageCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(EmptySenderException.class);
	}

	@Test
	public void testNullApplication() {
		//Given
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTOWithNullApplication();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = addressedMessageCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(EmptyApplicationException.class);
	}

	@Test
	public void testNullReceiverId() {
		//Given
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTOWithNullReceiver();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = addressedMessageCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(EmptyReceiverException.class);
	}

	@Test
	public void testInvalidSenderId() {
		//Given
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTOWithInvalidSender();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = addressedMessageCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(InvalidUserException.class);
	}

	@Test
	public void testSendMessageToOneself() {
		//Given
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTOToOneself();
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = addressedMessageCreationDTO::validate;

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(EqualSenderReceiverException.class);
	}

	@Test
	public void testValidDTO() throws InvalidDTOException {
		//Given
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAValidMessageCreationDTO();

		//When
		addressedMessageCreationDTO.validate();
		//Then
		assertThat(addressedMessageCreationDTO.getReceiverId()).isEqualTo(1l);
		assertThat(addressedMessageCreationDTO.getSenderId()).isEqualTo(2l);
		assertThat(addressedMessageCreationDTO.getText()).isEqualTo("Bla");
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTOToOneself() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();
		addressedMessageCreationDTO.setSenderId(1l);
		addressedMessageCreationDTO.setText("Bla");
		addressedMessageCreationDTO.setReceiverId(1l);
		addressedMessageCreationDTO.setApplication("CRACK_ME");
		return addressedMessageCreationDTO;
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTOWithInvalidSender() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();
		addressedMessageCreationDTO.setSenderId(0l);
		addressedMessageCreationDTO.setText("Bla");
		addressedMessageCreationDTO.setReceiverId(1l);
		addressedMessageCreationDTO.setApplication("CRACK_ME");
		return addressedMessageCreationDTO;
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTOWithNullReceiver() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();
		addressedMessageCreationDTO.setSenderId(2l);
		addressedMessageCreationDTO.setText("Bla");
		addressedMessageCreationDTO.setApplication("CRACK_ME");
		return addressedMessageCreationDTO;
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTOWithNullMessage() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();
		addressedMessageCreationDTO.setSenderId(1l);
		addressedMessageCreationDTO.setReceiverId(2l);
		addressedMessageCreationDTO.setApplication("CRACK_ME");
		return addressedMessageCreationDTO;
	}

	private AddressedMessageCreationDTO givenAValidMessageCreationDTO() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = new AddressedMessageCreationDTO();
		addressedMessageCreationDTO.setReceiverId(1l);
		addressedMessageCreationDTO.setSenderId(2l);
		addressedMessageCreationDTO.setText("Bla");
		addressedMessageCreationDTO.setApplication("CRACK_ME");
		return addressedMessageCreationDTO;
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTOWithNullSender() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();
		addressedMessageCreationDTO.setReceiverId(2l);
		addressedMessageCreationDTO.setText("Bla");
		addressedMessageCreationDTO.setApplication("CRACK_ME");
		return addressedMessageCreationDTO;
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTOWithNullApplication() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();
		addressedMessageCreationDTO.setSenderId(1L);
		addressedMessageCreationDTO.setReceiverId(2l);
		addressedMessageCreationDTO.setText("Bla");
		return addressedMessageCreationDTO;
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTO() {
		return new AddressedMessageCreationDTO();
	}
}
