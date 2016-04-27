package com.etermax.conversations.test.unit.service;

import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.ConversationFactory;
import com.etermax.conversations.factory.UserFactory;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationComparator;
import com.etermax.conversations.notification.service.NotificationService;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.EventService;
import com.etermax.conversations.service.impl.ConversationServiceImpl;
import com.etermax.conversations.test.integration.Given;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversationServiceTest {
	@Test
	public void deleteConversationUserNotInConversationTest() throws InvalidConversation, InvalidUserException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(new HashSet<>());
		when(conversationRepository.getConversationWithId("1")).thenReturn(conversation);
		ConversationFactory conversationFactory = mock(ConversationFactory.class);
		ConversationComparator conversationComparator = mock(ConversationComparator.class);
		UserFactory userFactory = mock(UserFactory.class);
		EventService eventService = mock(EventService.class);
		NotificationService notificationService = mock(NotificationService.class);
		ConversationService conversationService = new ConversationServiceImpl(conversationRepository,
				conversationFactory, conversationComparator, userFactory, eventService,notificationService);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationService.deleteConversation("1", 3l, "A2", new Date());

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(DeleteConversationException.class)
				.hasCauseInstanceOf(UserNotInConversationException.class);
	}

	@Test
	public void deleteNonExistentConversationTest() {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		doThrow(new ConversationNotFoundException()).when(conversationRepository).getConversationWithId(anyString());
		ConversationFactory conversationFactory = mock(ConversationFactory.class);
		ConversationComparator conversationComparator = mock(ConversationComparator.class);
		UserFactory userFactory = mock(UserFactory.class);
		EventService eventService = mock(EventService.class);
		NotificationService notificationService = mock(NotificationService.class);
		ConversationService conversationService = new ConversationServiceImpl(conversationRepository,
				conversationFactory, conversationComparator, userFactory, eventService, notificationService);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationService.deleteConversation("1", 1l, "A2", new Date());

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(DeleteConversationException.class)
				.hasCauseInstanceOf(ConversationNotFoundException.class);
	}
}
