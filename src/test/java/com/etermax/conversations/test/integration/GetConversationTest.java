package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.ConversationCreationDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.dto.TextMessageCreationDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.error.GetConversationException;
import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.ConversationMessagesResource;
import com.etermax.conversations.resource.ConversationResource;
import com.etermax.conversations.resource.ConversationsQueryResource;
import com.etermax.conversations.resource.ConversationsResource;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GetConversationTest {
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
	public void testGetNonExistentConversation() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		ConversationResource conversationResource = givenAConversationResource(conversationAdapter);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationResource.getConversation("1");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
		assertThatThrownBy(throwingCallable).hasCauseInstanceOf(GetConversationException.class);
		assertThatThrownBy(throwingCallable).hasRootCauseInstanceOf(ConversationNotFoundException.class);
	}

	@Test
	public void getConversationsWithMessages() throws ModelException {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		ConversationsQueryResource conversationsQueryResource = givenAConversationsQueryResource(conversationAdapter);
		ConversationsResource conversationsResource = new ConversationsResource(conversationAdapter);
		ConversationMessagesResource conversationMessagesResource = givenAConversationMessagesResource(messageAdapter);

		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO(Sets.newHashSet(1l, 2l));
		conversationsResource.saveConversation(conversationCreationDTO);

		ConversationCreationDTO anotherConversationCreationDTO = givenAnotherConversationCreationDTO(
				Sets.newHashSet(1l, 3l));
		conversationsResource.saveConversation(anotherConversationCreationDTO);

		TextMessageCreationDTO textMessageCreationDTO = Given.givenATextConversationMessageCreationDTO("Bla");
		conversationMessagesResource.saveMessage("1", textMessageCreationDTO);
		TextMessageCreationDTO anotherMessageCreationDTO = Given.givenATextConversationMessageCreationDTO("Bla Bla");
		conversationMessagesResource.saveMessage("1", anotherMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		//When
		List<ConversationDisplayDTO> conversationDisplayDTOs = conversationsQueryResource.getConversations(1l);

		//Then
		assertThat(conversationDisplayDTOs.size()).isEqualTo(2);

	}

	private ConversationsQueryResource givenAConversationsQueryResource(ConversationAdapter conversationAdapter) {
		return new ConversationsQueryResource(conversationAdapter);
	}

	private ConversationCreationDTO givenAnotherConversationCreationDTO(HashSet<Long> users) {
		ConversationCreationDTO anotherConversationCreationDTO = new ConversationCreationDTO();
		anotherConversationCreationDTO.setUsers(users);
		return anotherConversationCreationDTO;
	}

	private ConversationCreationDTO givenAConversationCreationDTO(HashSet<Long> users) {
		ConversationCreationDTO conversationCreationDTO = new ConversationCreationDTO();
		conversationCreationDTO.setUsers(users);
		return conversationCreationDTO;
	}

	private ConversationMessagesResource givenAConversationMessagesResource(MessageAdapter messageAdapter) {
		return new ConversationMessagesResource(messageAdapter);
	}

	private ConversationResource givenAConversationResource(ConversationAdapter conversationAdapter) {
		return new ConversationResource(conversationAdapter);
	}

}
