package com.etermax.conversations.test.unit.service;

import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.ConversationMessageFactory;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.User;
import com.etermax.conversations.notification.service.NotificationService;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.retrocompatibility.service.XMPPRetrocompatibilityMessageService;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.EventService;
import com.etermax.conversations.service.MessageService;
import com.etermax.conversations.service.impl.MessageServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageServiceTest {

	@Test
	public void saveMessageUserNotInConversation()
			throws InvalidConversation, InvalidUserException, InvalidMessageException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(new HashSet<>());
		when(conversationRepository.getConversationWithId(anyString())).thenReturn(conversation);
		MessageService messageService = givenAMessageService(conversationRepository);

		ConversationMessage conversationMessage = mock(ConversationMessage.class);
		when(conversationMessage.getSender()).thenReturn(new User(1l));

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> messageService.saveMessage(conversationMessage, "1");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(SaveMessageException.class)
				.hasCauseInstanceOf(UserNotInConversationException.class);
	}

	private MessageService givenAMessageService(ConversationRepository conversationRepository) {
		ConversationMessageFactory conversationMessageFactory = mock(ConversationMessageFactory.class);
		ConversationService conversationService = mock(ConversationService.class);
		EventService eventService = mock(EventService.class);
		NotificationService notificationService = mock(NotificationService.class);
		XMPPRetrocompatibilityMessageService retrocompatibilityMessageService = mock(XMPPRetrocompatibilityMessageService.class);
		return new MessageServiceImpl(conversationRepository, conversationMessageFactory, conversationService,
				eventService, notificationService, retrocompatibilityMessageService);
	}

	@Test
	public void deleteMessageNotFoundTest() throws InvalidConversation, InvalidUserException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		when(conversationRepository.getConversationMessage("1", "1", 1l)).thenThrow(MessageNotFoundException.class);
		MessageService messageService = givenAMessageService(conversationRepository);
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> messageService.deleteMessage("1", "1", 1l, "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(DeleteMessageException.class);
	}
}
