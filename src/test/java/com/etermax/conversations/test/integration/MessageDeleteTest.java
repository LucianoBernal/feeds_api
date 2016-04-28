package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.*;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.*;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MessageDeleteTest {
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
	public void testMessageDelete() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);

		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);
		conversationsResource.saveConversation(conversationCreationDTO);

		TextMessageCreationDTO textMessageCreationDTO = Given.givenATextConversationMessageCreationDTO("Bla");

		ConversationMessagesResource conversationMessagesResource = givenAConversationMessagesResource(messageAdapter);
		ConversationDataDTO conversationDataDTO = conversationMessagesResource.saveMessage("1", textMessageCreationDTO);
		TextMessageDisplayDTO textMessageDisplayDTO = (TextMessageDisplayDTO) conversationDataDTO;

		HistoryResource historyResource = givenAConversationMessagesQueryResource(synchronizationAdapter);

		ConversationMessageDeletionResource conversationMessageDeletionResource = givenAConversationMessageDeletionResource(
				messageAdapter);
		ConversationMessageDeletionDTO deletionDTO = givenAMessageConversationDeletionDTO();
		deletionDTO.setUserId(1l);
		deletionDTO.setApplication("A2");

		conversationMessageDeletionResource.deleteMessage("1", textMessageDisplayDTO.getId(), deletionDTO);
		//When
		Given.flushRepository(conversationRepositoryFactory);
		HistoryDTO messagesForUserOne = historyResource.getMessagesFromId(1l, "1", 0l, 1l, "A2");
		HistoryDTO messagesForUserTwo = historyResource.getMessagesFromId(2l, "1", 0l, 10000000000000l, "A2");
		//Then
		assertThat(messagesForUserOne.getConversationDataDTO()).isEmpty();
		TextMessageDisplayDTO conversationTextMessage = (TextMessageDisplayDTO) messagesForUserTwo.getConversationDataDTO().get(
				0);
		assertThat(messagesForUserTwo.getConversationDataDTO().size()).isEqualTo(1);
		assertThat(conversationTextMessage.getText()).isEqualTo("Bla");
		assertThat(conversationTextMessage.getSenderId()).isEqualTo(1l);
	}

	@Test
	public void testMessageDeleteWithEvents() throws InterruptedException {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given
				.givenASynchronizationAdapter(conversationRepositoryFactory);

		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);
		conversationsResource.saveConversation(conversationCreationDTO);

		TextMessageCreationDTO textMessageCreationDTO = Given.givenATextConversationMessageCreationDTO("Bla");

		ConversationMessagesResource conversationMessagesResource = givenAConversationMessagesResource(messageAdapter);

		Date dateBeforeSave = new Date();
		Thread.sleep(1);
		ConversationDataDTO conversationDataDTO1 = conversationMessagesResource.saveMessage("1", textMessageCreationDTO);
		TextMessageDisplayDTO textMessageDisplayDTO = (TextMessageDisplayDTO) conversationDataDTO1;
		Thread.sleep(1);
		Date dateAfterSave = new Date();

		SyncResource syncResource = new SyncResource(synchronizationAdapter);

		ConversationMessageDeletionResource conversationMessageDeletionResource = givenAConversationMessageDeletionResource(
				messageAdapter);
		ConversationMessageDeletionDTO deletionDTO = givenAMessageConversationDeletionDTO();
		deletionDTO.setUserId(1l);
		deletionDTO.setApplication("A2");

		conversationMessageDeletionResource.deleteMessage("1", textMessageDisplayDTO.getId(), deletionDTO);
		Given.flushRepository(conversationRepositoryFactory);

		//When
		List<SyncDTO> syncBefore = syncResource.getConversationSync(1l, String.valueOf(dateBeforeSave.getTime()), "A2");
		List<SyncDTO> syncAfter = syncResource.getConversationSync(1l, String.valueOf(dateAfterSave.getTime()), "A2");

		//Then
		assertThat(syncBefore).isEmpty();
		assertThat(syncAfter).hasSize(1);
		SyncDTO syncDTO = syncAfter.get(0);
		assertThat(syncDTO.getConversationId()).isEqualTo("1");
		assertThat(syncDTO.getConversationData()).hasSize(1);
		ConversationDataDTO conversationDataDTO = syncDTO.getConversationData().get(0);
		assertThat(conversationDataDTO.getType()).isEqualTo("event");
		EventDTO eventDTO = (EventDTO) conversationDataDTO;
		assertThat(eventDTO.getKey()).isEqualTo("DELETE_MESSAGE");
	}

	@Test
	public void testDeleteMessageInNonExistentConversation() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		ConversationMessageDeletionResource conversationMessageDeletionResource = givenAConversationMessageDeletionResource(
				messageAdapter);
		ConversationMessageDeletionDTO deletionDTO = givenAMessageConversationDeletionDTO();
		deletionDTO.setUserId(1l);
		deletionDTO.setApplication("A2");

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationMessageDeletionResource
				.deleteMessage("1", "1-2-3-4-5-6-7-8", deletionDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(RuntimeException.class);
	}

	private ConversationMessageDeletionDTO givenAMessageConversationDeletionDTO() {
		return new ConversationMessageDeletionDTO();
	}

	private ConversationMessageDeletionResource givenAConversationMessageDeletionResource(
			MessageAdapter messageAdapter) {
		return new ConversationMessageDeletionResource(messageAdapter);
	}

	private HistoryResource givenAConversationMessagesQueryResource(SynchronizationAdapter synchronizationAdapter) {
		return new HistoryResource(synchronizationAdapter);
	}

	private ConversationsResource givenAConversationsResource(ConversationAdapter conversationAdapter) {
		return new ConversationsResource(conversationAdapter);
	}

	private ConversationCreationDTO givenAConversationCreationDTO() {
		ConversationCreationDTO conversationCreationDTO = new ConversationCreationDTO();
		conversationCreationDTO.setUsers(Sets.newHashSet(1l, 2l));
		return conversationCreationDTO;
	}

	private ConversationMessagesResource givenAConversationMessagesResource(MessageAdapter messageAdapter) {
		return new ConversationMessagesResource(messageAdapter);
	}
}
