package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.TextMessageCreationDTO;
import com.etermax.conversations.dto.TextMessageDisplayDTO;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.model.MessageVisitor;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextMessageCreationDTOTest {

	@Test
	public void testAccesorsTextMessageDisplayDTO() {
		//GIVEN
		TextMessageCreationDTO textMessageCreationDTO = new TextMessageCreationDTO();

		//WHEN
		textMessageCreationDTO.setSenderId(1l);
		textMessageCreationDTO.setText("hola");

		//THEN
		assertThat(textMessageCreationDTO.getSenderId()).isEqualTo(1l);
		assertThat(textMessageCreationDTO.getText()).isEqualTo("hola");
	}

	@Test
	public void testInvalidTextInTextMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		TextMessageCreationDTO textMessageCreationDTO = new TextMessageCreationDTO();

		//WHEN
		textMessageCreationDTO.setSenderId(1l);
		textMessageCreationDTO.setText("");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> textMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage(
				"");
	}

	@Test
	public void testInvalidSenderInTextMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		TextMessageCreationDTO textMessageCreationDTO = new TextMessageCreationDTO();

		//WHEN
		textMessageCreationDTO.setSenderId(null);
		textMessageCreationDTO.setText("hola");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> textMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage("");
	}

	@Test
	public void testAcceptTextMessageCreationDTO() {
		//GIVEN
		TextMessageCreationDTO textMessageCreationDTO = new TextMessageCreationDTO();
		MessageVisitor messageVisitor = mock(MessageVisitor.class);
		when(messageVisitor.saveMessage(textMessageCreationDTO, "1")).thenReturn(mock(TextMessageDisplayDTO.class));

		//WHEN
		TextMessageDisplayDTO messageConversationDisplayDTO = (TextMessageDisplayDTO) textMessageCreationDTO.accept(messageVisitor, "1");

		//THEN
		assertThat(messageConversationDisplayDTO).isInstanceOf(TextMessageDisplayDTO.class);
	}
}