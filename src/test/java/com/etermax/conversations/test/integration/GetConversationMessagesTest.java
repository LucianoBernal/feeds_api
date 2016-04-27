package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.error.GetConversationMessagesException;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.HistoryResource;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GetConversationMessagesTest {
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
	public void testGetMessagesFromIdFromNonExistentConversation() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);
		HistoryResource historyResource = givenAConversationMessagesQueryResource(synchronizationAdapter);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> historyResource.getMessagesFromId(1l, "1", 1l, 1l, "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(GetConversationMessagesException.class);
		assertThatThrownBy(throwingCallable).hasRootCauseInstanceOf(ConversationNotFoundException.class);
	}

	private HistoryResource givenAConversationMessagesQueryResource(SynchronizationAdapter synchronizationAdapter) {
		return new HistoryResource(synchronizationAdapter);
	}

}
