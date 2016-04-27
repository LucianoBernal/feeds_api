package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.metrics.NoneNotificationMetricsPublisher;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityConversationAdapter;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityUserAdapter;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityConversationDTO;
import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.etermax.conversations.retrocompatibility.resource.RetrocompatibilityMessagesResource;
import com.etermax.conversations.retrocompatibility.migration.service.MigrationService;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityConversationService;
import com.etermax.conversations.service.ConversationService;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import dto.UserDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RetrocompatibilityMessagesTest {
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
	public void sendMessageTest() {
		//Given
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		RetrocompatibilityUserAdapter retrocompatibilityUserAdapter = givenAUserAdapter();
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);

		ConversationService conversationService = Given.givenAConversationService();
		RetrocompatibilityConversationService retrocompatibilityConversationService = givenARetrocompatibilityConversationService();
		MigrationRepository oldRepository = mock(MigrationRepository.class);
		when(oldRepository.checkAndSetMigration(anyLong())).thenReturn(true);
		RetrocompatibilityConversationAdapter adapter = new RetrocompatibilityConversationAdapter(
				new MigrationService(oldRepository, null), conversationAdapter, messageAdapter,
				retrocompatibilityUserAdapter, conversationService, retrocompatibilityConversationService);

		RetrocompatibilityMessagesResource retrocompatibilityMessagesResource = getRetrocompatibilityMessagesResource(
				adapter);
		AddressedMessageCreationDTO addressedMessageCreationDTO = givenAMessageCreationDTO();

		//When
		retrocompatibilityMessagesResource.saveMessage(addressedMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		retrocompatibilityMessagesResource.saveMessage(addressedMessageCreationDTO);
		Given.flushRepository(conversationRepositoryFactory);
		//Then
		RetrocompatibilityConversationDTO conversation = retrocompatibilityMessagesResource.getMessages("1,2", "A2");
		assertThat(conversation.getTotal()).isEqualTo(2);
		assertThat(conversation.getList()).extracting("message").contains("Bla");

	}

	private RetrocompatibilityConversationService givenARetrocompatibilityConversationService() {
		return new RetrocompatibilityConversationService(Given.givenAConversationRepositoryFactory().createRepository());
	}

	@Test
	public void getMessagesTest() {
		//Given
		RetrocompatibilityConversationService retrocompatibilityConversationService = givenARetrocompatibilityConversationService();
		ConversationRepositoryFactory conversationRepositoryFactory = Given.givenAConversationRepositoryFactory();
		MessageAdapter messageAdapter = Given.givenAMessageAdapter(conversationRepositoryFactory);
		ConversationAdapter conversationAdapter = Given.givenAConversationAdapter(conversationRepositoryFactory);

		RetrocompatibilityUserAdapter retrocompatibilityUserAdapter = givenAUserAdapter();

		ConversationService conversationService = Given.givenAConversationService();
		MigrationRepository oldRepository = mock(MigrationRepository.class);
		when(oldRepository.checkAndSetMigration(anyLong())).thenReturn(true);
		RetrocompatibilityConversationAdapter adapter = new RetrocompatibilityConversationAdapter(
				new MigrationService(oldRepository, null), conversationAdapter, messageAdapter,
				retrocompatibilityUserAdapter, conversationService, retrocompatibilityConversationService);
		RetrocompatibilityMessagesResource retrocompatibilityMessagesResource = getRetrocompatibilityMessagesResource(
				adapter);

		//When
		RetrocompatibilityConversationDTO messages = retrocompatibilityMessagesResource.getMessages("1,2", "A2");

		//Then
		assertThat(messages.getTotal()).isEqualTo(0);
		assertThat(messages.getList()).hasSize(0);

	}

	private RetrocompatibilityUserAdapter givenAUserAdapter() {
		RetrocompatibilityUserAdapter retrocompatibilityUserAdapter = mock(RetrocompatibilityUserAdapter.class);
		UserDTO userDTO = mock(UserDTO.class);
		HashMap<String, Object> extensions = new HashMap<>();
		HashMap<Object, Object> socialInteractions = Maps.newHashMap();
		socialInteractions.put("is_blocked", false);
		extensions.put("social_interactions", socialInteractions);
		when(userDTO.getExtensions()).thenReturn(extensions);
		when(retrocompatibilityUserAdapter.getUser(anyList())).thenReturn(userDTO);
		return retrocompatibilityUserAdapter;
	}

	private AddressedMessageCreationDTO givenAMessageCreationDTO() {
		AddressedMessageCreationDTO addressedMessageCreationDTO = new AddressedMessageCreationDTO();
		addressedMessageCreationDTO.setText("Bla");
		addressedMessageCreationDTO.setSenderId(1l);
		addressedMessageCreationDTO.setReceiverId(2l);
		addressedMessageCreationDTO.setApplication("A2");
		addressedMessageCreationDTO.setBlocked(false);
		return addressedMessageCreationDTO;
	}

	private RetrocompatibilityMessagesResource getRetrocompatibilityMessagesResource(
			RetrocompatibilityConversationAdapter adapter) {
		return new RetrocompatibilityMessagesResource(adapter, new NoneNotificationMetricsPublisher());
	}

}
