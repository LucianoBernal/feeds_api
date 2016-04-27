package com.etermax.conversations.test.unit.model;

import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.factory.ConversationMessageFactory;
import com.etermax.conversations.factory.UserFactory;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.HasMore;
import com.etermax.conversations.model.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HasMoreMessageTest {

	@Test
	public void testHasMore() throws InvalidUserException, InvalidMessageException {
		//Given

		//When
		HasMore hasMoreMessage = new HasMore(4, new Date(3), new Date(5));

		//Then
		assertThat(hasMoreMessage.getHasMore()).isEqualTo(true);
		assertThat(hasMoreMessage.getTotalMessages()).isEqualTo(4);
		assertThat(hasMoreMessage.getFirstDate().getTime()).isEqualTo(3l);
		assertThat(hasMoreMessage.getLastDate().getTime()).isEqualTo(5l);
	}

}
