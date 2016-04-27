package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.etermax.conversations.model.AddressedMessage;
import com.etermax.conversations.model.User;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class AddressedMessageDisplayDTOTest {

	@Test
	public void testMessageDisplayDTO() throws Exception {
		//GIVEN
		User sender = new User(1L);
		User receiver = new User(2L);
		Date date = new Date();
		String text = "Test";
		String application = "A2";

		AddressedMessage addressedMessage = new AddressedMessage(text, sender, receiver, date, application, false);
		addressedMessage.setId("1-eeb6c77b-0756-450e-b9a6-93704ee1a11c-128-34258");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		//WHEN
		AddressedMessageDisplayDTO displayDTO = new AddressedMessageDisplayDTO(addressedMessage);

		//THEN
		assertThat(displayDTO.getDate()).isCloseTo(addressedMessage.getDate().getTime(), within(5000l));
		assertThat(displayDTO.getText()).isEqualTo(addressedMessage.getText());
		assertThat(displayDTO.getApplication()).isEqualTo(addressedMessage.getApplication());
		assertThat(displayDTO.getSenderId()).isEqualTo(addressedMessage.getSender().getId());
		assertThat(displayDTO.getReceiverId()).isEqualTo(addressedMessage.getUser().getId());
		assertThat(displayDTO.getId()).isEqualTo("1-eeb6c77b-0756-450e-b9a6-93704ee1a11c-128-34258");
	}

}
