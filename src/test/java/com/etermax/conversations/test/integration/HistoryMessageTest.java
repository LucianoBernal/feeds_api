package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.dto.HasMoreDTO;
import com.etermax.conversations.dto.HistoryDTO;
import com.etermax.conversations.dto.TextMessageDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.HistoryResource;
import com.etermax.conversations.resource.MessagesResource;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HistoryMessageTest {
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
	public void testHistoryConversationNotFound() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);
		HistoryResource historyResource = givenAHistoryResource(synchronizationAdapter);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> historyResource.getMessagesFromId(1l, "1", null, null, "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasRootCauseInstanceOf(ConversationNotFoundException.class);

	}

	private HistoryResource givenAHistoryResource(SynchronizationAdapter synchronizationAdapter) {
		return new HistoryResource(synchronizationAdapter);
	}

	@Test
	public void testHistoryRangeNoMore() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);

		HistoryResource historyResource = givenAHistoryResource(synchronizationAdapter);
		MessagesResource messagesResource = givenAMessagesResource(messageAdapter);
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();

		//When
		messagesResource.saveMessage(addressedMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		messagesResource.saveMessage(addressedMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		HistoryDTO historyDTO = historyResource.getMessagesFromId(1l, "1", null, null, "A2");

		//Then
		assertThat(historyDTO.getHasMore().getHasMore()).isEqualTo(false);
		assertThat(historyDTO.getConversationDataDTO().size()).isEqualTo(2);
		TextMessageDisplayDTO message = (TextMessageDisplayDTO) historyDTO.getConversationDataDTO().get(0);
		assertThat(message.getText()).isEqualTo("Bla");
		assertThat(message.getApplication()).isEqualTo("A2");
		assertThat(message.getSenderId()).isEqualTo(1l);

	}

	@Test
	public void testHistoryNoRangeHasMore() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(conversationRepositoryFactory);

		HistoryResource historyResource = givenAHistoryResource(synchronizationAdapter);
		MessagesResource messagesResource = givenAMessagesResource(messageAdapter);
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();

		//When
		messagesResource.saveMessage(addressedMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		messagesResource.saveMessage(addressedMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		messagesResource.saveMessage(addressedMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		HistoryDTO historyDTO = historyResource
				.getMessagesFromId(1l, "1", null, null, "A2");

		//Then
		HasMoreDTO hasMoreMessage = historyDTO.getHasMore();
		assertThat(hasMoreMessage.getHasMore()).isEqualTo(true);
		assertThat(hasMoreMessage.getTotalMessages()).isEqualTo(1);
		assertThat(historyDTO.getConversationDataDTO().size()).isEqualTo(2);
		TextMessageDisplayDTO message = (TextMessageDisplayDTO) historyDTO.getConversationDataDTO().get(0);
		assertThat(message.getText()).isEqualTo("Bla");
		assertThat(message.getApplication()).isEqualTo("A2");
		assertThat(message.getSenderId()).isEqualTo(1l);
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTO() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = new AddressedMessageCreationDTO();
		addressedMessageCreationDTO.setText("Bla");
		addressedMessageCreationDTO.setSenderId(1l);
		addressedMessageCreationDTO.setReceiverId(2l);
		addressedMessageCreationDTO.setApplication("A2");
		return addressedMessageCreationDTO;
	}

	private MessagesResource givenAMessagesResource(MessageAdapter messageAdapter) {
		return new MessagesResource(messageAdapter);
	}

}
