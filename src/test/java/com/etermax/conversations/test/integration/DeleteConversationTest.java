package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.*;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.error.DeleteConversationException;
import com.etermax.conversations.error.UserNotInConversationException;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.ConversationDeletionResource;
import com.etermax.conversations.resource.ConversationMessagesResource;
import com.etermax.conversations.resource.ConversationsResource;
import com.etermax.conversations.resource.HistoryResource;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DeleteConversationTest {

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
	public void deleteConversationTest() throws InterruptedException {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given.givenASynchronizationAdapter(
				conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);

		HistoryResource historyResource = givenAHistoryResource(synchronizationAdapter);
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);
		ConversationMessagesResource conversationMessagesResource = givenAConversationMessagesResource(messageAdapter);
		ConversationDeletionResource conversationDeletionResource = givenAConversationDeletionResource(
				conversationAdapter);
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		conversationsResource.saveConversation(conversationCreationDTO);
		TextMessageCreationDTO conversationMessageCreationDTO = Given.givenATextConversationMessageCreationDTO("Bla");
		conversationMessagesResource.saveMessage("1", conversationMessageCreationDTO);
		ConversationMessageDeletionDTO conversationMessageDeletionDTO = givenAMessageConversationDeletionDTO();

		//When
		Thread.sleep(1l); //creacion del mensaje y borrado en distintos momentos
		conversationDeletionResource.deleteConversation("1", conversationMessageDeletionDTO);
		Given.flushRepository(conversationRepositoryFactory);
		HistoryDTO conversationHistoryUserOne = historyResource
				.getMessagesFromId(1l, "1", null, null, "A2");
		HistoryDTO conversationHistoryUserTwo = historyResource
				.getMessagesFromId(2l, "1", null, null, "A2");

		//Then
		assertThat(conversationHistoryUserOne.getConversationDataDTO()).hasSize(1);
		ConversationDataDTO conversationDataDTO = conversationHistoryUserOne.getConversationDataDTO().get(0);
		assertThat(conversationDataDTO.getType()).isEqualTo("event");
		EventDTO event = (EventDTO) conversationDataDTO;
		assertThat(event.getKey()).isEqualTo("DELETE_CONVERSATION");
		assertThat(conversationHistoryUserTwo.getConversationDataDTO().size()).isEqualTo(1);
		assertThat(conversationHistoryUserTwo.getConversationDataDTO()).extracting("text").containsExactly("Bla");
	}

	@Test
	public void deleteNonExistentConversationTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		ConversationDeletionResource conversationDeletionResource = givenAConversationDeletionResource(
				conversationAdapter);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationDeletionResource
				.deleteConversation("1", givenAMessageConversationDeletionDTO());

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(DeleteConversationException.class)
				.hasRootCauseInstanceOf(ConversationNotFoundException.class);
	}

	@Test
	public void deleteUserNotInConversationTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);
		ConversationDeletionResource conversationDeletionResource = givenAConversationDeletionResource(
				conversationAdapter);

		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		conversationsResource.saveConversation(conversationCreationDTO);
		ConversationMessageDeletionDTO conversationMessageDeletionDTO = new ConversationMessageDeletionDTO();
		conversationMessageDeletionDTO.setUserId(11l);
		conversationMessageDeletionDTO.setApplication("A2");

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationDeletionResource
				.deleteConversation("1", conversationMessageDeletionDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(DeleteConversationException.class)
				.hasRootCauseInstanceOf(UserNotInConversationException.class);

	}

	private ConversationMessageDeletionDTO givenAMessageConversationDeletionDTO() {
		ConversationMessageDeletionDTO conversationMessageDeletionDTO = new ConversationMessageDeletionDTO();
		conversationMessageDeletionDTO.setUserId(1l);
		conversationMessageDeletionDTO.setApplication("A2");
		return conversationMessageDeletionDTO;
	}

	private ConversationDeletionResource givenAConversationDeletionResource(ConversationAdapter conversationAdapter) {
		return new ConversationDeletionResource(conversationAdapter);
	}

	private ConversationMessagesResource givenAConversationMessagesResource(MessageAdapter messageAdapter) {
		return new ConversationMessagesResource(messageAdapter);
	}

	private ConversationsResource givenAConversationsResource(ConversationAdapter conversationAdapter) {
		return new ConversationsResource(conversationAdapter);
	}

//	private TextMessageCreationDTO givenATextConversationMessageCreationDTO() {
//		TextMessageCreationDTO textCreationDTO = new TextMessageCreationDTO();
//		textCreationDTO.setSenderId(1l);
//		textCreationDTO.setText("Bla");
//		return textCreationDTO;
//	}

	private ConversationCreationDTO givenAConversationCreationDTO() {
		ConversationCreationDTO conversationCreationDTO = new ConversationCreationDTO();
		conversationCreationDTO.setUsers(Sets.newHashSet(1l, 2l));
		return conversationCreationDTO;
	}

	private HistoryResource givenAHistoryResource(SynchronizationAdapter synchronizationAdapter) {
		return new HistoryResource(synchronizationAdapter);
	}

}
