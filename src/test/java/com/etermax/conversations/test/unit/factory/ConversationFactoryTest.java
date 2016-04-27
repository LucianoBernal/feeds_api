package com.etermax.conversations.test.unit.factory;

import com.etermax.conversations.factory.ConversationFactory;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.User;
import com.google.common.collect.Sets;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConversationFactoryTest {

	@Test
	public void testValidConversation() throws Exception {
		//GIVEN
		ConversationFactory factory = new ConversationFactory();

		//WHEN
		Conversation conversation = factory.createConversation(Sets.newHashSet(new User(1L), new User(2L)));

		//THEN
		assertThat(conversation).isNotNull();
	}

}
