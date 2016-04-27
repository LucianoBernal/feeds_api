package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.ReceiptAdapter;
import com.etermax.conversations.dto.ConversationCreationDTO;
import com.etermax.conversations.dto.IndividualMessageReceiptCreationDTO;
import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.ConversationsResource;
import com.etermax.conversations.resource.MessageReceiptsResource;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SaveReceiptsTest {
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
	public void saveReceiptInNonExistentConversationTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ReceiptAdapter receiptAdapter = Given.givenAReceiptAdapter(conversationRepositoryFactory);
		MessageReceiptsResource messageReceiptsResource = givenAMessageReceiptsResource(receiptAdapter);
		IndividualMessageReceiptCreationDTO messageReceiptDTO = new IndividualMessageReceiptCreationDTO();
		messageReceiptDTO.setType("received");
		messageReceiptDTO.setUserId(1l);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> messageReceiptsResource
				.saveReceipt("1", "1", messageReceiptDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(SaveReceiptException.class)
				.hasRootCauseInstanceOf(ConversationNotFoundException.class);
	}

	@Test
	public void saveReceiptInSomeoneElseConversationTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ReceiptAdapter receiptAdapter = Given.givenAReceiptAdapter(conversationRepositoryFactory);
		MessageReceiptsResource messageReceiptsResource = givenAMessageReceiptsResource(receiptAdapter);
		IndividualMessageReceiptCreationDTO messageReceiptDTO = new IndividualMessageReceiptCreationDTO();
		messageReceiptDTO.setType("received");
		messageReceiptDTO.setUserId(1l);

		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		conversationsResource.saveConversation(conversationCreationDTO);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> messageReceiptsResource
				.saveReceipt("1", "1", messageReceiptDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(SaveReceiptException.class)
				.hasRootCauseInstanceOf(UserNotInConversationException.class);
	}

	@Test
	public void saveReceiptForNonExistentMessage() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ReceiptAdapter receiptAdapter = Given.givenAReceiptAdapter(conversationRepositoryFactory);
		MessageReceiptsResource messageReceiptsResource = givenAMessageReceiptsResource(receiptAdapter);
		IndividualMessageReceiptCreationDTO messageReceiptDTO = new IndividualMessageReceiptCreationDTO();
		messageReceiptDTO.setType("received");
		messageReceiptDTO.setUserId(2l);

		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		conversationsResource.saveConversation(conversationCreationDTO);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> messageReceiptsResource
				.saveReceipt("1", "1-2-3-4-5-6-7-8", messageReceiptDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(SaveReceiptException.class).hasRootCauseInstanceOf(MessageNotFoundException.class);
	}

	private ConversationCreationDTO givenAConversationCreationDTO() {
		ConversationCreationDTO conversationCreationDTO = new ConversationCreationDTO();
		conversationCreationDTO.setUsers(Sets.newHashSet(3l, 2l));
		return conversationCreationDTO;
	}

	private MessageReceiptsResource givenAMessageReceiptsResource(ReceiptAdapter receiptAdapter) {
		return new MessageReceiptsResource(receiptAdapter);
	}

	private ConversationsResource givenAConversationsResource(ConversationAdapter conversationAdapter) {
		return new ConversationsResource(conversationAdapter);
	}

}
