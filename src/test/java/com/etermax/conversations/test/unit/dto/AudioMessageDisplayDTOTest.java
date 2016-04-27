package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.AudioMessageDisplayDTO;
import com.etermax.conversations.dto.ConversationMessageDisplayDTO;
import com.etermax.conversations.model.ConversationAudioMessage;
import com.etermax.conversations.model.User;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class AudioMessageDisplayDTOTest {

	@Test
	public void testAudioMessageDisplayDTO() throws Exception {
		//GIVEN
		ConversationAudioMessage conversationAudioMessage = new ConversationAudioMessage(new User(1l), "1", new Date(),
				"url@url", 10l, "mp3", "A2", false);
		conversationAudioMessage.setId("1");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		//WHEN
		ConversationMessageDisplayDTO displayDTO = new AudioMessageDisplayDTO(conversationAudioMessage);

		//THEN
		assertThat(displayDTO).isInstanceOf(AudioMessageDisplayDTO.class);
		AudioMessageDisplayDTO audioDisplayDTO = (AudioMessageDisplayDTO) displayDTO;
		assertThat(audioDisplayDTO.getDate()).isCloseTo(conversationAudioMessage.getDate().getTime(), within(5000l));
		assertThat(audioDisplayDTO.getUrl()).isEqualTo(conversationAudioMessage.getUrl());
		assertThat(audioDisplayDTO.getLength()).isEqualTo(conversationAudioMessage.getLength());
		assertThat(audioDisplayDTO.getFormat()).isEqualTo(conversationAudioMessage.getFormat());
		assertThat(audioDisplayDTO.getMessageType()).isEqualTo("audio");
		assertThat(audioDisplayDTO.getApplication()).isEqualTo("A2");
		assertThat(audioDisplayDTO.getSenderId()).isEqualTo(conversationAudioMessage.getSender().getId());
		assertThat(audioDisplayDTO.getId()).isEqualTo(conversationAudioMessage.getId());
	}

}
