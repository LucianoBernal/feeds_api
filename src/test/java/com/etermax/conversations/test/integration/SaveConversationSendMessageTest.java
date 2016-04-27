package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.*;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.error.SaveMessageException;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.ConversationMessagesResource;
import com.etermax.conversations.resource.ConversationsResource;
import com.etermax.conversations.resource.HistoryResource;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SaveConversationSendMessageTest {
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
	public void testSaveConversationSendMessageTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		SynchronizationAdapter synchronizationAdapter = Given
				.givenASynchronizationAdapter(conversationRepositoryFactory);

		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);
		conversationsResource.saveConversation(conversationCreationDTO);

		TextMessageCreationDTO conversationMessageCreationDTO = Given.givenATextConversationMessageCreationDTO("Bla");

		ConversationMessagesResource conversationMessagesResource = givenAConversationMessagesResource(messageAdapter);
		HistoryResource historyResource = givenAHistoryResource(synchronizationAdapter);

		//When
		conversationMessagesResource.saveMessage("1", conversationMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		HistoryDTO historyDTO = historyResource.getMessagesFromId(1l, "1", null, null, "A2");

		//Then
		List<ConversationDataDTO> conversationDataDTOList = historyDTO.getConversationDataDTO();
		ConversationDataDTO conversationDataDTO = conversationDataDTOList.get(0);
		assertThat(conversationDataDTO).isInstanceOf(TextMessageDisplayDTO.class);
		TextMessageDisplayDTO textMessageDisplayDTO = (TextMessageDisplayDTO) conversationDataDTO;
		assertThat(conversationDataDTOList.size()).isEqualTo(1);
		assertThat(textMessageDisplayDTO.getSenderId()).isEqualTo(1l);
		assertThat(textMessageDisplayDTO.getText()).isEqualTo("Bla");
		assertThat(textMessageDisplayDTO.getDate()).isNotNull();
	}

	@Test
	public void testSendMessageInNonExistentConversation() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);

		ConversationMessagesResource conversationMessagesResource = givenAConversationMessagesResource(messageAdapter);
		TextMessageCreationDTO conversationMessageCreationDTO = Given.givenATextConversationMessageCreationDTO("Bla");
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationMessagesResource
				.saveMessage("1", conversationMessageCreationDTO);
		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(SaveMessageException.class);
		assertThatThrownBy(throwingCallable).hasRootCauseInstanceOf(ConversationNotFoundException.class);
	}

	@Test
	public void testSendInvalidMessage() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);

		ConversationMessagesResource conversationMessagesResource = givenAConversationMessagesResource(messageAdapter);
		TextMessageCreationDTO conversationMessageCreationDTO = new TextMessageCreationDTO();
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationMessagesResource
				.saveMessage("1", conversationMessageCreationDTO);
		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(InvalidDTOException.class);

	}

	private ConversationMessagesResource givenAConversationMessagesResource(MessageAdapter messageAdapter) {
		return new ConversationMessagesResource(messageAdapter);
	}

	private ConversationsResource givenAConversationsResource(ConversationAdapter conversationAdapter) {
		return new ConversationsResource(conversationAdapter);
	}

	private ConversationCreationDTO givenAConversationCreationDTO() {
		ConversationCreationDTO conversationCreationDTO = new ConversationCreationDTO();
		conversationCreationDTO.setUsers(Sets.newHashSet(1l, 2l));
		return conversationCreationDTO;
	}

	private HistoryResource givenAHistoryResource(SynchronizationAdapter synchronizationAdapter) {
		return new HistoryResource(synchronizationAdapter);
	}

}
