package com.etermax.conversations.test.unit.service;

import com.etermax.conversations.error.*;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationHistory;
import com.etermax.conversations.model.ConversationSync;
import com.etermax.conversations.model.Range;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.SynchronizationService;
import com.etermax.conversations.service.impl.SynchronizationServiceImpl;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SynchronizationServiceTest {
	@Test
	public void invalidDateGetUserReceiptsTest() {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		SynchronizationService synchronizationService = new SynchronizationServiceImpl(conversationRepository);

		//When
		ThrowableAssert.ThrowingCallable invalidDateString = () -> synchronizationService
				.getConversationSync(1l, "InvalidDateString", "A2");

		//Then
		assertThatThrownBy(invalidDateString).isInstanceOf(GetUserDataException.class);
	}

	@Test
	public void invalidGetUserReceiptsTest() throws ModelException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		when(conversationRepository.getConversationSyncData(anyLong(), anyList(), any(Date.class), anyString()))
				.thenThrow(ModelException.class);
		SynchronizationService synchronizationService = new SynchronizationServiceImpl(conversationRepository);

		//When
		ThrowableAssert.ThrowingCallable invalidDateString = () -> synchronizationService
				.getConversationSync(1l, "", "A2");

		//Then
		assertThatThrownBy(invalidDateString).isInstanceOf(GetUserDataException.class);
	}

	@Test
	public void getReceiptHistoryTest()
			throws ConversationNotFoundException, GetReceiptHistoryException, ModelException,
			GetConversationMessagesException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(Sets.newHashSet(1l));
		when(conversationRepository.getConversationWithId(anyString())).thenReturn(conversation);
		when(conversationRepository.getConversationHistory(anyString(), any(Range.class), any(), anyString()))
				.thenReturn(mock(ConversationHistory.class));
		SynchronizationService synchronizationService = new SynchronizationServiceImpl(conversationRepository);

		//When
		ConversationHistory receiptHistory = synchronizationService.getConversationHistory("1", new Range(1l, 2l), 1l,
				"A2");

		//Then
		assertThat(receiptHistory).isInstanceOf(ConversationHistory.class);
		assertThat(receiptHistory).isNotNull();
	}

	@Test
	public void conversationNotFoundGetReceiptHistoryTest() throws ConversationNotFoundException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		when(conversationRepository.getConversationWithId("1"))
				.thenThrow(mock(ConversationNotFoundException.class));
		SynchronizationService synchronizationService = new SynchronizationServiceImpl(conversationRepository);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> synchronizationService
				.getConversationHistory("1", new Range(1l, 2l), 2l, "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(GetConversationMessagesException.class)
				.hasCauseInstanceOf(ConversationNotFoundException.class);
	}

	@Test
	public void userNotInConversationGetReceiptHistoryTest() throws ConversationNotFoundException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(new HashSet<>());
		when(conversationRepository.getConversationWithId("1")).thenReturn(conversation);
		SynchronizationService synchronizationService = new SynchronizationServiceImpl(conversationRepository);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> synchronizationService
				.getConversationHistory("1", new Range(1l, 1l), 2l, "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(GetConversationMessagesException.class)
				.hasCauseInstanceOf(UserNotInConversationException.class);

	}
}
