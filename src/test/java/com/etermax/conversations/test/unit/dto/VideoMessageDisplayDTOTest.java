package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.ConversationMessageDisplayDTO;
import com.etermax.conversations.dto.VideoMessageDisplayDTO;
import com.etermax.conversations.model.ConversationVideoMessage;
import com.etermax.conversations.model.User;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class VideoMessageDisplayDTOTest {

	@Test
	public void testVideoMessageDisplayDTO() throws Exception {
		//GIVEN
		ConversationVideoMessage conversationVideoMessage = new ConversationVideoMessage(new User(1l), "1", new Date(),
				"url@url", "preview-thumbnail",10l, "avi", "l", "A2", false);
		conversationVideoMessage.setId("1");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		//WHEN
		ConversationMessageDisplayDTO displayDTO = new VideoMessageDisplayDTO(conversationVideoMessage);

		//THEN
		assertThat(displayDTO).isInstanceOf(VideoMessageDisplayDTO.class);
		VideoMessageDisplayDTO videoDisplayDTO = (VideoMessageDisplayDTO) displayDTO;
		assertThat(videoDisplayDTO.getDate()).isCloseTo(conversationVideoMessage.getDate().getTime(), within(5000l));
		assertThat(videoDisplayDTO.getUrl()).isEqualTo(conversationVideoMessage.getUrl());
		assertThat(videoDisplayDTO.getLength()).isEqualTo(conversationVideoMessage.getLength());
		assertThat(videoDisplayDTO.getFormat()).isEqualTo(conversationVideoMessage.getFormat());
		assertThat(videoDisplayDTO.getThumbnail()).isEqualTo(conversationVideoMessage.getThumbnail());
		assertThat(videoDisplayDTO.getMessageType()).isEqualTo("video");
		assertThat(videoDisplayDTO.getSenderId()).isEqualTo(conversationVideoMessage.getSender().getId());
		assertThat(videoDisplayDTO.getId()).isEqualTo(conversationVideoMessage.getId());
	}
}
