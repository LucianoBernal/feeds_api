package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.User;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConversationDisplayDTOTest {

	@Test
	public void testConversationDisplayDTO() throws Exception {
		//GIVEN
		Conversation conversation = new Conversation(Sets.newHashSet(new User(1L), new User(2L)));

		//WHEN
		ConversationDisplayDTO displayDTO = new ConversationDisplayDTO(conversation);

		//THEN
		assertThat(displayDTO.getUsers()
				.equals(conversation.getUsers().stream().map(User::getId).collect(Collectors.toList())));
	}

	@Test
	public void testGetIdConversationDisplayDTO() throws Exception {
		//GIVEN
		Conversation conversation = new Conversation(Sets.newHashSet(new User(1L), new User(2L)));
		conversation.setId("1");

		//WHEN
		ConversationDisplayDTO displayDTO = new ConversationDisplayDTO(conversation);

		//THEN
		assertThat(displayDTO.getId().equals(1l));
	}

}
