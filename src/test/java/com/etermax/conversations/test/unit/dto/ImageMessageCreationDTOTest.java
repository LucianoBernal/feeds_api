package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.ImageMessageCreationDTO;
import com.etermax.conversations.dto.ImageMessageDisplayDTO;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.model.MessageVisitor;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageMessageCreationDTOTest {

	@Test
	public void testAccesorsImageMessageDisplayDTO() {
		//GIVEN
		ImageMessageCreationDTO imageMessageCreationDTO = new ImageMessageCreationDTO();

		//WHEN
		imageMessageCreationDTO.setSenderId(1l);
		imageMessageCreationDTO.setUrl("url@url");
		imageMessageCreationDTO.setThumbnail("thumbnail");
		imageMessageCreationDTO.setFormat("mp3");

		//THEN
		assertThat(imageMessageCreationDTO.getSenderId()).isEqualTo(1l);
		assertThat(imageMessageCreationDTO.getUrl()).isEqualTo("url@url");
		assertThat(imageMessageCreationDTO.getThumbnail()).isEqualTo("thumbnail");
		assertThat(imageMessageCreationDTO.getFormat()).isEqualTo("mp3");
	}

	@Test
	public void testInvalidUrlImageMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		ImageMessageCreationDTO imageMessageCreationDTO = new ImageMessageCreationDTO();

		//WHEN
		imageMessageCreationDTO.setSenderId(1l);
		imageMessageCreationDTO.setThumbnail("asf");
		imageMessageCreationDTO.setUrl("");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> imageMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage(
				"");
	}

	@Test
	public void testInvalidSenderImageMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		ImageMessageCreationDTO imageMessageCreationDTO = new ImageMessageCreationDTO();

		//WHEN
		imageMessageCreationDTO.setSenderId(null);
		imageMessageCreationDTO.setThumbnail("sdf");
		imageMessageCreationDTO.setUrl("url");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> imageMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage("");
	}

	@Test
	public void testInvalidThumbnailImageMessageDisplayDTO() throws InvalidDTOException {
		//GIVEN
		ImageMessageCreationDTO imageMessageCreationDTO = new ImageMessageCreationDTO();

		//WHEN
		imageMessageCreationDTO.setSenderId(1l);
		imageMessageCreationDTO.setThumbnail(null);
		imageMessageCreationDTO.setUrl("url");
		ThrowableAssert.ThrowingCallable throwingCallable = () -> imageMessageCreationDTO.validate();

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(InvalidDTOException.class).hasCauseInstanceOf(Exception.class).hasMessage("");
	}

	@Test
	public void testAcceptImageMessageCreationDTO() {
		//GIVEN
		ImageMessageCreationDTO imageMessageCreationDTO = new ImageMessageCreationDTO();
		MessageVisitor messageVisitor = mock(MessageVisitor.class);
		when(messageVisitor.saveMessage(imageMessageCreationDTO, "1")).thenReturn(mock(ImageMessageDisplayDTO.class));

		//WHEN
		ImageMessageDisplayDTO messageConversationDisplayDTO = (ImageMessageDisplayDTO) imageMessageCreationDTO.accept(messageVisitor, "1");

		//THEN
		assertThat(messageConversationDisplayDTO).isInstanceOf(ImageMessageDisplayDTO.class);
	}
}
