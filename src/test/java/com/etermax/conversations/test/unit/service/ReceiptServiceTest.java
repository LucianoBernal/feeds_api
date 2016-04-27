package com.etermax.conversations.test.unit.service;

import com.etermax.conversations.error.*;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.model.User;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.ReceiptService;
import com.etermax.conversations.service.impl.ReceiptServiceImpl;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReceiptServiceTest {



	@Test
	public void saveReceiptInMessageTest()
			throws SaveReceiptException, MessageNotFoundException, ConversationNotFoundException,
			UserNotInConversationException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		ConversationMessage conversationMessage = mock(ConversationMessage.class);
		User user = mock(User.class);
		when(user.getId()).thenReturn(2l);
		when(conversationMessage.getSender()).thenReturn(user);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(Sets.newHashSet(1l));
		when(conversationRepository.getConversationWithId("1")).thenReturn(conversation);
		when(conversationRepository.getConversationMessage(anyString(), anyString(), anyLong()))
				.thenReturn(conversationMessage);
		when(conversationRepository.saveReceiptInMessage(anyString(), anyString(), any()))
				.thenReturn(mock(IndividualMessageReceipt.class));
		ReceiptService receiptService = new ReceiptServiceImpl(conversationRepository);
		IndividualMessageReceipt messageReceipt = mock(IndividualMessageReceipt.class);
		when(messageReceipt.getUser()).thenReturn(1l);

		//When
		IndividualMessageReceipt individualMessageReceipt = receiptService.saveReceiptInMessage("1", "1", messageReceipt);

		//Then
		assertThat(individualMessageReceipt).isNotNull();
	}

	@Test
	public void messageNotFoundSaveReceiptInMessageTest()
			throws MessageNotFoundException, ConversationNotFoundException, UserNotInConversationException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(Sets.newHashSet(1l));
		when(conversationRepository.getConversationWithId("1")).thenReturn(conversation);
		ReceiptService receiptService = new ReceiptServiceImpl(conversationRepository);
		when(conversationRepository.getConversationMessage(anyString(), anyString(), anyLong()))
				.thenThrow(MessageNotFoundException.class);
		IndividualMessageReceipt receipt = mock(IndividualMessageReceipt.class);
		when(receipt.getUser()).thenReturn(1l);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> receiptService.saveReceiptInMessage("1", "1", receipt);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(SaveReceiptException.class)
				.hasCauseInstanceOf(MessageNotFoundException.class);
	}

	@Test
	public void ackMyOwnMessageSaveReceiptInMessageTest()
			throws MessageNotFoundException, ConversationNotFoundException, UserNotInConversationException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(Sets.newHashSet(1l));
		when(conversationRepository.getConversationWithId("1")).thenReturn(conversation);
		ConversationMessage conversationMessage = mock(ConversationMessage.class);
		User user = mock(User.class);
		when(user.getId()).thenReturn(2l);
		when(conversationMessage.getSender()).thenReturn(user);
		when(conversationRepository.getConversationMessage(anyString(), anyString(), anyLong()))
				.thenReturn(conversationMessage);
		when(conversationRepository.saveReceiptInMessage(anyString(), anyString(), any()))
				.thenReturn(mock(IndividualMessageReceipt.class));
		ReceiptService receiptService = new ReceiptServiceImpl(conversationRepository);
		IndividualMessageReceipt messageReceipt = mock(IndividualMessageReceipt.class);
		when(messageReceipt.getUser()).thenReturn(2l);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> receiptService
				.saveReceiptInMessage("1", "1", messageReceipt);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(SaveReceiptException.class);
	}


	@Test
	public void doubleAckTest() throws SaveReceiptException {
		//Given
		ConversationRepository conversationRepository = mock(ConversationRepository.class);
		ConversationMessage conversationMessage = mock(ConversationMessage.class);
		User user = mock(User.class);
		when(user.getId()).thenReturn(2l);
		when(conversationMessage.getSender()).thenReturn(user);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(Sets.newHashSet(1l));
		when(conversationRepository.getConversationWithId("1")).thenReturn(conversation);
		when(conversationRepository.getConversationMessage(anyString(), anyString(), anyLong()))
				.thenReturn(conversationMessage);
		when(conversationRepository.isAlreadyAcknowledged(anyString(), anyString(), any())).thenReturn(true);
		ReceiptService receiptService = new ReceiptServiceImpl(conversationRepository);
		IndividualMessageReceipt messageReceipt = mock(IndividualMessageReceipt.class);
		when(messageReceipt.getUser()).thenReturn(1l);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> receiptService.saveReceiptInMessage("1", "1", messageReceipt);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(AlreadyAcknowledgedMessageException.class);
	}
}
