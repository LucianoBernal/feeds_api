package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SaveMessageTest {
	Lock sequential = new ReentrantLock();

	@Before
	public void setUp(){
		sequential.lock();
	}

	@After
	public void tearDown(){
		sequential.unlock();
	}

	@Test
	public void saveInvalidMessageTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);

		MessagesResource messagesResource = givenAMessagesResource(messageAdapter);

		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAnInvalidMessageCreationDTO();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> messagesResource
				.saveMessage(addressedMessageCreationDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(InvalidDTOException.class);
	}

	private AddressedMessageCreationDTO givenAnInvalidMessageCreationDTO() {
		return new AddressedMessageCreationDTO();
	}

	private MessagesResource givenAMessagesResource(MessageAdapter messageAdapter) {
		return new MessagesResource(messageAdapter);
	}

}
