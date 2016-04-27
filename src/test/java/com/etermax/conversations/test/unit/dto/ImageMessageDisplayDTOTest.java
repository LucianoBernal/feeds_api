package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.ConversationMessageDisplayDTO;
import com.etermax.conversations.dto.ImageMessageDisplayDTO;
import com.etermax.conversations.model.ConversationImageMessage;
import com.etermax.conversations.model.User;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ImageMessageDisplayDTOTest {

	@Test
	public void testImageMessageDisplayDTO() throws Exception {
		//GIVEN
		ConversationImageMessage conversationImageMessage = new ConversationImageMessage(new User(1l), "1", new Date(),
				"url@url", "preview-thumbnail", "jpg", "l", "A2", false);
		conversationImageMessage.setId("1");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		//WHEN
		ConversationMessageDisplayDTO displayDTO = new ImageMessageDisplayDTO(conversationImageMessage);

		//THEN
		assertThat(displayDTO).isInstanceOf(ImageMessageDisplayDTO.class);
		ImageMessageDisplayDTO imageDisplayDTO = (ImageMessageDisplayDTO) displayDTO;
		assertThat(imageDisplayDTO.getDate()).isCloseTo(conversationImageMessage.getDate().getTime(), within(5000l));
		assertThat(imageDisplayDTO.getUrl()).isEqualTo(conversationImageMessage.getUrl());
		assertThat(imageDisplayDTO.getFormat()).isEqualTo(conversationImageMessage.getFormat());
		assertThat(imageDisplayDTO.getThumbnail()).isEqualTo(conversationImageMessage.getThumbnail());
		assertThat(imageDisplayDTO.getOrientation()).isEqualTo(conversationImageMessage.getOrientation());
		assertThat(imageDisplayDTO.getMessageType()).isEqualTo("image");
		assertThat(imageDisplayDTO.getSenderId()).isEqualTo(conversationImageMessage.getSender().getId());
		assertThat(imageDisplayDTO.getId()).isEqualTo(conversationImageMessage.getId());
	}
}
