package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.dto.SyncDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.GetUserDataException;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.ConversationResource;
import com.etermax.conversations.resource.MessagesResource;
import com.etermax.conversations.resource.SyncResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SyncMessagesTest {
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
	public void testSyncMessagesWithInvalidDate() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);

		MessagesResource messagesResource = givenAMessagesResource(messageAdapter);
		ConversationResource conversationResource = new ConversationResource(conversationAdapter);
		SyncResource syncMessagesResource = new SyncResource(synchronizationAdapter);

		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();

		//When
		messagesResource
				.saveMessage(addressedMessageCreationDTO);
		conversationResource.getConversation("1");

		assertThatThrownBy(() -> syncMessagesResource.getConversationSync(1l, "abc", "A2")).isInstanceOf(
				ClientException.class)
				.hasCauseInstanceOf(GetUserDataException.class).hasRootCauseInstanceOf(NumberFormatException.class);
	}

	@Test
	public void testSyncMessagesWithFutureDate() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);

		MessagesResource messagesResource = givenAMessagesResource(messageAdapter);
		ConversationResource conversationResource = new ConversationResource(conversationAdapter);
		SyncResource syncMessagesResource = new SyncResource(synchronizationAdapter);

		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();

		//When
		messagesResource.saveMessage(addressedMessageCreationDTO);
		conversationResource.getConversation("1");

		Given.flushRepository(conversationRepositoryFactory);
		List<SyncDTO> syncDTOs = syncMessagesResource.getConversationSync(1l, "16725668400000", "A2"); //1/6/2500

		//Then
		assertThat(syncDTOs.size()).isEqualTo(0);

	}

	@Test
	public void testSyncHasMore() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);

		MessagesResource messagesResource = givenAMessagesResource(messageAdapter);
		ConversationResource conversationResource = new ConversationResource(conversationAdapter);
		SyncResource syncMessagesResource = new SyncResource(synchronizationAdapter);

		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();

		//When
		messagesResource.saveMessage(addressedMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		messagesResource.saveMessage(addressedMessageCreationDTO);
		messagesResource.saveMessage(addressedMessageCreationDTO);
		messagesResource.saveMessage(addressedMessageCreationDTO);
		conversationResource.getConversation("1");
		Given.flushRepository(conversationRepositoryFactory);
		List<SyncDTO> syncDTOs = syncMessagesResource.getConversationSync(1l, "947127600000", "A2"); //1/6/2000
		SyncDTO syncDTO = syncDTOs.get(0);

		//Then
		assertThat(syncDTOs.size()).isEqualTo(1);
		assertThat(syncDTO.getHasMore().getHasMore()).isEqualTo(true);
		assertThat(syncDTO.getHasMore().getTotalMessages()).isEqualTo(2);
		assertThat(syncDTO.getConversationId()).isEqualTo("1");
		assertThat(syncDTO.getConversationData().size()).isEqualTo(2);
	}

	@Test
	public void testSyncNoMore() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);

		MessagesResource messagesResource = givenAMessagesResource(messageAdapter);
		ConversationResource conversationResource = new ConversationResource(conversationAdapter);
		SyncResource syncMessagesResource = new SyncResource(synchronizationAdapter);

		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();

		//When
		messagesResource.saveMessage(addressedMessageCreationDTO);
		conversationResource.getConversation("1");
		Given.flushRepository(conversationRepositoryFactory);
		List<SyncDTO> syncDTOs = syncMessagesResource.getConversationSync(1l, "947127600000", "A2");
		SyncDTO syncDTO = syncDTOs.get(0);

		//Then
		assertThat(syncDTOs.size()).isEqualTo(1);
		assertThat(syncDTO.getHasMore().getHasMore()).isEqualTo(false);
		assertThat(syncDTO.getHasMore().getTotalMessages()).isEqualTo(0);
		assertThat(syncDTO.getConversationId()).isEqualTo("1");
		assertThat(syncDTO.getConversationData().size()).isEqualTo(1);

	}

	private MessagesResource givenAMessagesResource(MessageAdapter messageAdapter) {
		return new MessagesResource(messageAdapter);
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTO() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = new AddressedMessageCreationDTO();
		addressedMessageCreationDTO.setText("Bla");
		addressedMessageCreationDTO.setSenderId(1l);
		addressedMessageCreationDTO.setReceiverId(2l);
		addressedMessageCreationDTO.setApplication("A2");
		return addressedMessageCreationDTO;
	}

}
