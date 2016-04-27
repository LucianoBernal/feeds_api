package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.AudioMessageCreationDTO;
import com.etermax.conversations.dto.AudioMessageDisplayDTO;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.model.MessageVisitor;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AudioMessageCreationDTOTest {

	@Test
	public void testAccesorsAudioMessageDisplayDTO() {
		//GIVEN
		AudioMessageCreationDTO audioMessageCreationDTO = new AudioMessageCreationDTO();

		//WHEN
		audioMessageCreationDTO.setSenderId(1l);
		audioMessageCreationDTO.setUrl("url@url");
		audioMessageCreationDTO.setLength(10l);
		audioMessageCreationDTO.setFormat("mp3");

		//THEN
		assertThat(audioMessageCreationDTO.getSenderId()).isEqualTo(1l);
		assertThat(audioMessageCreationDTO.getUrl()).isEqualTo("url@url");
		assertThat(audioMessageCreationDTO.getLength()).isEqualTo(10l);
		assertThat(audioMessageCreationDTO.getFormat()).isEqualTo("mp3");
	}

	@Test
	public void testInvalidUrlAudioMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		AudioMessageCreationDTO audioMessageCreationDTO = new AudioMessageCreationDTO();

		//WHEN
		audioMessageCreationDTO.setSenderId(1l);
		audioMessageCreationDTO.setLength(10l);
		audioMessageCreationDTO.setUrl("");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> audioMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage(
				"");
	}

	@Test
	public void testInvalidSenderAudioMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		AudioMessageCreationDTO audioMessageCreationDTO = new AudioMessageCreationDTO();

		//WHEN
		audioMessageCreationDTO.setSenderId(null);
		audioMessageCreationDTO.setLength(10l);
		audioMessageCreationDTO.setUrl("url");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> audioMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage("");
	}

	@Test
	public void testInvalidLengthAudioMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		AudioMessageCreationDTO audioMessageCreationDTO = new AudioMessageCreationDTO();

		//WHEN
		audioMessageCreationDTO.setSenderId(1l);
		audioMessageCreationDTO.setLength(null);
		audioMessageCreationDTO.setUrl("url");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> audioMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage("");
	}

	@Test
	public void testAcceptAudioMessageCreationDTO() {
		//GIVEN
		AudioMessageCreationDTO audioMessageCreationDTO = new AudioMessageCreationDTO();
		MessageVisitor messageVisitor = mock(MessageVisitor.class);
		when(messageVisitor.saveMessage(audioMessageCreationDTO, "1")).thenReturn(mock(AudioMessageDisplayDTO.class));

		//WHEN
		AudioMessageDisplayDTO messageConversationDisplayDTO = (AudioMessageDisplayDTO) audioMessageCreationDTO.accept(messageVisitor, "1");

		//THEN
		assertThat(messageConversationDisplayDTO).isInstanceOf(AudioMessageDisplayDTO.class);
	}
}
