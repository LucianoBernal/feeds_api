package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationCreationDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.resource.ConversationsQueryResource;
import com.etermax.conversations.resource.ConversationsResource;
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

public class SaveConversationTest {
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
	public void sameConversationTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);

		ConversationsQueryResource conversationsQueryResource = givenAConversationsQueryResource(conversationAdapter);

		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTO();
		ConversationDisplayDTO conversationDisplayDTO = conversationsResource.saveConversation(conversationCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		//When
		ConversationDisplayDTO anotherConversationDisplayDTO = conversationsResource
				.saveConversation(conversationCreationDTO);

		List<ConversationDisplayDTO> conversationDisplayDTOList = conversationsQueryResource.getConversations(1l);
		//Then
		assertThat(anotherConversationDisplayDTO.getId().equals(conversationDisplayDTO.getId()));
		assertThat(conversationDisplayDTOList.size()).isEqualTo(1);
		assertThat(conversationDisplayDTOList.get(0).getUsers()).containsExactly(1l, 2l);
		assertThat(conversationDisplayDTOList.get(0).getId()).isEqualTo("1");
	}

	@Test
	public void notTheSameConversationTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);

		ConversationsQueryResource conversationsQueryResource = givenAConversationsQueryResource(conversationAdapter);
		ConversationCreationDTO conversationCreationDTO = givenAConversationCreationDTOWithThreeParticipants();
		ConversationDisplayDTO conversationDisplayDTO = conversationsResource.saveConversation(conversationCreationDTO);
		//When
		ConversationDisplayDTO anotherConversationDisplayDTO = conversationsResource
				.saveConversation(conversationCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		List<ConversationDisplayDTO> conversationDisplayDTOList = conversationsQueryResource.getConversations(1l);
		//Then
		assertThat(!anotherConversationDisplayDTO.getId().equals(conversationDisplayDTO.getId()));
		assertThat(conversationDisplayDTOList.size()).isEqualTo(2);
		assertThat(conversationDisplayDTOList.get(0).getUsers()).containsExactly(1l, 2l, 3l);
		assertThat(conversationDisplayDTOList.get(0).getId()).isEqualTo("2");
		assertThat(conversationDisplayDTOList.get(1).getId()).isEqualTo("1");

	}

	@Test
	public void testSaveInvalidConversation() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);
		ConversationsResource conversationsResource = givenAConversationsResource(conversationAdapter);
		ConversationCreationDTO conversationCreationDTO = givenAnInvalidConversationCreationDTO();

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationsResource
				.saveConversation(conversationCreationDTO);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);

	}

	private ConversationCreationDTO givenAnInvalidConversationCreationDTO() {
		return new ConversationCreationDTO();
	}

	private ConversationCreationDTO givenAConversationCreationDTOWithThreeParticipants() {
		ConversationCreationDTO conversationCreationDTO = new ConversationCreationDTO();
		conversationCreationDTO.setUsers(Sets.newHashSet(1l, 2l, 3l));
		return conversationCreationDTO;
	}

	private ConversationsQueryResource givenAConversationsQueryResource(ConversationAdapter conversationAdapter) {
		return new ConversationsQueryResource(conversationAdapter);
	}

	private ConversationsResource givenAConversationsResource(ConversationAdapter conversationAdapter) {
		return new ConversationsResource(conversationAdapter);
	}

	private ConversationCreationDTO givenAConversationCreationDTO() {
		ConversationCreationDTO conversationCreationDTO = new ConversationCreationDTO();
		conversationCreationDTO.setUsers(Sets.newHashSet(1l, 2l));
		return conversationCreationDTO;
	}

}
