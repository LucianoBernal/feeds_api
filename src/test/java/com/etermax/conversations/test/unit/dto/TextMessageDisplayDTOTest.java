package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.ConversationMessageDisplayDTO;
import com.etermax.conversations.dto.TextMessageDisplayDTO;
import com.etermax.conversations.model.ConversationTextMessage;
import com.etermax.conversations.model.User;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class TextMessageDisplayDTOTest {

	@Test
	public void testTextMessageDisplayDTO() throws Exception {
		//GIVEN
		ConversationTextMessage conversationTextMessage = new ConversationTextMessage(new User(1l), "1", new Date(),
				"Test", "A2", false);
		conversationTextMessage.setId("1");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		//WHEN
		ConversationMessageDisplayDTO displayDTO = new TextMessageDisplayDTO(conversationTextMessage);

		//THEN
		assertThat(displayDTO).isInstanceOf(TextMessageDisplayDTO.class);
		TextMessageDisplayDTO textDisplayDTO = (TextMessageDisplayDTO) displayDTO;
		assertThat(textDisplayDTO.getDate()).isCloseTo(conversationTextMessage.getDate().getTime(), within(5000l));
		assertThat(textDisplayDTO.getText()).isEqualTo(conversationTextMessage.getText());
		assertThat(textDisplayDTO.getMessageType()).isEqualTo("text");
		assertThat(textDisplayDTO.getSenderId()).isEqualTo(conversationTextMessage.getSender().getId());
		assertThat(textDisplayDTO.getId()).isEqualTo(conversationTextMessage.getId());
	}
}
