package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.VideoMessageCreationDTO;
import com.etermax.conversations.dto.VideoMessageDisplayDTO;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.model.MessageVisitor;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VideoMessageCreationDTOTest {

	@Test
	public void testAccesorsVideoMessageDisplayDTO() {
		//GIVEN
		VideoMessageCreationDTO videoMessageCreationDTO = new VideoMessageCreationDTO();

		//WHEN
		videoMessageCreationDTO.setSenderId(1l);
		videoMessageCreationDTO.setUrl("url@url");
		videoMessageCreationDTO.setLength(10l);
		videoMessageCreationDTO.setThumbnail("thumbnail");
		videoMessageCreationDTO.setFormat("mp3");

		//THEN
		assertThat(videoMessageCreationDTO.getSenderId()).isEqualTo(1l);
		assertThat(videoMessageCreationDTO.getUrl()).isEqualTo("url@url");
		assertThat(videoMessageCreationDTO.getLength()).isEqualTo(10l);
		assertThat(videoMessageCreationDTO.getThumbnail()).isEqualTo("thumbnail");
		assertThat(videoMessageCreationDTO.getFormat()).isEqualTo("mp3");
	}

	@Test
	public void testInvalidUrlVideoMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		VideoMessageCreationDTO videoMessageCreationDTO = new VideoMessageCreationDTO();

		//WHEN
		videoMessageCreationDTO.setSenderId(1l);
		videoMessageCreationDTO.setLength(10l);
		videoMessageCreationDTO.setThumbnail("thumb");
		videoMessageCreationDTO.setUrl("");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> videoMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage(
				"");
	}

	@Test
	public void testInvalidSenderVideoMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		VideoMessageCreationDTO videoMessageCreationDTO = new VideoMessageCreationDTO();

		//WHEN
		videoMessageCreationDTO.setSenderId(null);
		videoMessageCreationDTO.setLength(10l);
		videoMessageCreationDTO.setThumbnail("thumb");
		videoMessageCreationDTO.setUrl("url");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> videoMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage("");
	}

	@Test
	public void testInvalidLengthVideoMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		VideoMessageCreationDTO videoMessageCreationDTO = new VideoMessageCreationDTO();

		//WHEN
		videoMessageCreationDTO.setSenderId(1l);
		videoMessageCreationDTO.setLength(null);
		videoMessageCreationDTO.setThumbnail("thumb");
		videoMessageCreationDTO.setUrl("url");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> videoMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage("");
	}

	@Test
	public void testInvalidThumbnailVideoMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		VideoMessageCreationDTO videoMessageCreationDTO = new VideoMessageCreationDTO();

		//WHEN
		videoMessageCreationDTO.setSenderId(1l);
		videoMessageCreationDTO.setLength(10l);
		videoMessageCreationDTO.setThumbnail("");
		videoMessageCreationDTO.setUrl("url");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> videoMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage("");
	}

	@Test
	public void testAcceptVideoMessageCreationDTO() {
		//GIVEN
		VideoMessageCreationDTO videoMessageCreationDTO = new VideoMessageCreationDTO();
		MessageVisitor messageVisitor = mock(MessageVisitor.class);
		when(messageVisitor.saveMessage(videoMessageCreationDTO, "1")).thenReturn(mock(VideoMessageDisplayDTO.class));

		//WHEN
		VideoMessageDisplayDTO messageConversationDisplayDTO = (VideoMessageDisplayDTO) videoMessageCreationDTO.accept(messageVisitor, "1");

		//THEN
		assertThat(messageConversationDisplayDTO).isInstanceOf(VideoMessageDisplayDTO.class);
	}
}
