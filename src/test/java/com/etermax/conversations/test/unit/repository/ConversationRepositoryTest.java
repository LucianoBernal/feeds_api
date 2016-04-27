package com.etermax.conversations.test.unit.repository;

import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.AddressedMessageFactory;
import com.etermax.conversations.model.*;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.repository.impl.memory.MemoryConversationRepository;
import com.etermax.conversations.test.integration.Given;
import com.google.common.collect.Sets;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversationRepositoryTest {


	private ConversationRepository givenAConversationRepository(AddressedMessageFactory addressedMessageFactory) {
		return Given.givenAConversationRepositoryFactory().createRepository();
	}

	@Test
	public void saveReceiptGetHistoryTest() throws InvalidRangeException, ModelException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		IndividualMessageReceipt receipt = new IndividualMessageReceipt(new ReceivedType(), 2l);
		saveConversation(conversationRepository);
		ConversationMessage conversationMessage = addMessageWithDate(conversationRepository, 1l);
		//When
		conversationRepository.saveReceiptInMessage("1", conversationMessage.getId(), receipt);
		conversationRepository.flush();
		ConversationHistory conversationHistory = conversationRepository
				.getConversationHistory("1", new Range(0l, 3l), 1l, "A2");
		//Then
		assertThat(conversationHistory.getConversationDataList()).hasSize(1);
		ConversationData conversationData = conversationHistory.getConversationDataList().get(0);
		ConversationTextMessage conversationTextMessage = (ConversationTextMessage) conversationData;
		assertThat(conversationTextMessage.getText()).isEqualTo("bla");
		assertThat(conversationTextMessage.getDate().getTime()).isEqualTo(1l);
		assertThat(conversationTextMessage.getSender().getId()).isEqualTo(1l);
		assertThat(conversationTextMessage.getApplication()).isEqualTo("A2");
		assertThat(conversationTextMessage.getConversationId()).isEqualTo("1");
		IndividualMessageReceipt individualMessageReceipt = conversationTextMessage.getMessageReceipt().getReceipts()
				.get(0);
		assertThat(individualMessageReceipt.getUser()).isEqualTo(2l);
		assertThat(individualMessageReceipt.getType()).isInstanceOf(ReceivedType.class);
	}

	@Test
	public void saveReceiptGetSameUserHistoryTest() throws InvalidRangeException, ModelException {
		//Given
		IndividualMessageReceipt receipt = mock(IndividualMessageReceipt.class);
		when(receipt.getUser()).thenReturn(2l);
		when(receipt.getType()).thenReturn(mock(ReceivedType.class));
		when(receipt.getDate()).thenReturn(new Date(2));
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		ConversationMessage savedConversationMessage = addMessage(conversationRepository);

		//When
		conversationRepository.saveReceiptInMessage("1", savedConversationMessage.getId(), receipt);
		conversationRepository.flush();
		ConversationHistory conversationHistory = conversationRepository
				.getConversationHistory("1", new Range(0l, 2l), 2l, "A2");
		//Then
		assertThat(conversationHistory.getConversationDataList()).hasSize(1);
		assertThat(conversationHistory.getConversationDataList().get(0)).isInstanceOf(ConversationMessage.class);
		ConversationMessage conversationMessage = (ConversationMessage) conversationHistory.getConversationDataList()
				.get(0);
		assertThat(conversationMessage.getMessageReceipt()).isNull();
		assertThat(conversationMessage.getDate().getTime()).isEqualTo(1l);
		assertThat(conversationMessage.getSender().getId()).isEqualTo(1l);
		assertThat(conversationMessage.getApplication()).isEqualTo("A2");
		assertThat(conversationMessage.getConversationId()).isEqualTo("1");
	}

	@Test
	public void saveReceiptGetHistoryWithPreviousDateTest() throws InvalidRangeException, ModelException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		IndividualMessageReceipt receipt = mock(IndividualMessageReceipt.class);
		when(receipt.getUser()).thenReturn(2l);
		when(receipt.getType()).thenReturn(mock(ReceivedType.class));
		when(receipt.getDate()).thenReturn(new Date());
		ConversationMessage conversationMessage = addMessage(conversationRepository);

		//When
		conversationRepository.saveReceiptInMessage("1", conversationMessage.getId(), receipt);
		conversationRepository.flush();
		ConversationHistory conversationHistory = conversationRepository
				.getConversationHistory("1", new Range(2l, 3l), 1l, "A2");
		//Then
		assertThat(conversationHistory.getConversationDataList()).hasSize(0);
	}

	private ConversationMessage addMessage(ConversationRepository conversationRepository) {
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(1), "bla",
				"A2", false);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getUserIds()).thenReturn(Sets.newHashSet(1l, 2l));
		when(conversation.getId()).thenReturn("1");

		conversationRepository.saveConversation(conversation);
		return conversationRepository.saveMessage(conversationMessage, conversation);
	}

	private ConversationMessage addMessageWithDate(ConversationRepository conversationRepository, long date) {
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(date), "bla",
				"A2", false);
		Conversation conversation = mock(Conversation.class);
		when(conversation.getId()).thenReturn("1");
		return conversationRepository.saveMessage(conversationMessage, conversation);
	}

	private Conversation saveConversation(ConversationRepository conversationRepository) {
		Conversation conversation = new Conversation(Sets.newHashSet(new User(1l), new User(2l)));
		conversationRepository.saveConversation(conversation);
		return conversation;
	}

	@Test
	public void saveReceiptGetMessageWithReceiptsTest() throws ModelException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		IndividualMessageReceipt receipt = mock(IndividualMessageReceipt.class);
		when(receipt.getUser()).thenReturn(2l);
		when(receipt.getDate()).thenReturn(new Date(2));
		when(receipt.getType()).thenReturn(new ReceivedType());
		saveConversation(conversationRepository);
		ConversationMessage conversationMessage = addMessageWithDate(conversationRepository, 1l);

		//When
		conversationRepository.saveReceiptInMessage("1", conversationMessage.getId(), receipt);
		conversationRepository.flush();
		List<ConversationSync> conversationSyncData = conversationRepository
				.getConversationSyncData(1l, Collections.singletonList("1"), new Date(0), "A2");
		//Then
		assertThat(conversationSyncData).hasSize(1);
		ConversationSync conversationSync = conversationSyncData.get(0);
		assertThat(conversationSync.getConversationId()).isEqualTo("1");
		assertThat(conversationSync.getConversationDataList()).hasSize(1);
		assertThat(conversationSync.getConversationDataList().get(0).getType()).isEqualTo("message");
		ConversationTextMessage conversationTextMessage = (ConversationTextMessage) conversationSync
				.getConversationDataList().get(0);
		IndividualMessageReceipt individualReceipts = conversationTextMessage.getMessageReceipt().getReceipts().get(0);
		assertThat(individualReceipts.getType()).isInstanceOf(ReceivedType.class);
		assertThat(individualReceipts.getUser()).isEqualTo(2l);
	}

	@Test
	public void saveReceiptGetOnlyReceiptSyncTest() {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		IndividualMessageReceipt receipt = new IndividualMessageReceipt(new ReceivedType(), 2l);
		ConversationMessage conversationMessage = addMessage(conversationRepository);
		conversationRepository.flush();

		//When
		conversationRepository.saveReceiptInMessage("1", conversationMessage.getId(), receipt);
		conversationRepository.flush();
		List<ConversationSync> conversationSyncData = conversationRepository
				.getConversationSyncData(1l, Collections.singletonList("1"), new Date(2), "A2");

		//Then
		assertThat(conversationSyncData).hasSize(1);
		ConversationSync conversationSync = conversationSyncData.get(0);
		assertThat(conversationSync.getConversationDataList()).hasSize(1);
		ConversationData conversationData = conversationSync.getConversationDataList().get(0);
		assertThat(conversationData.getType()).isEqualTo("receipt");
		MessageReceipt messageReceipt = (MessageReceipt) conversationData;
		assertThat(messageReceipt.getReceipts()).hasSize(1);
		IndividualMessageReceipt individualMessageReceipt = messageReceipt.getReceipts().get(0);
		assertThat(individualMessageReceipt.getType().toString()).isEqualTo("received");
		assertThat(individualMessageReceipt.getUser()).isEqualTo(2l);

	}

	@Test
	public void saveReceiptGetUserReceiptsWithFutureDateTest() throws ModelException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		IndividualMessageReceipt receipt = mock(IndividualMessageReceipt.class);
		when(receipt.getUser()).thenReturn(2l);
		when(receipt.getDate()).thenReturn(new Date(2));
		when(receipt.getType()).thenReturn(new ReceivedType());
		ConversationMessage conversationMessage = addMessage(conversationRepository);

		//When
		conversationRepository.saveReceiptInMessage("1", conversationMessage.getId(), receipt);
		List<ConversationSync> conversationSyncData = conversationRepository
				.getConversationSyncData(1l, Collections.singletonList("1"), new Date(3), "A2");
		//Then
		assertThat(conversationSyncData).hasSize(0);
	}

	@Test
	public void saveReceiptGetSameUserReceiptsTest() throws ModelException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		IndividualMessageReceipt receipt = new IndividualMessageReceipt(new ReceivedType(), 2l);
		saveConversation(conversationRepository);
		ConversationMessage savedconversationMessage = addMessageWithDate(conversationRepository, 1l);

		//When
		conversationRepository.saveReceiptInMessage("1", savedconversationMessage.getId(), receipt);
		conversationRepository.flush();
		List<ConversationSync> conversationSyncData = conversationRepository
				.getConversationSyncData(2l, Collections.singletonList("1"), new Date(0), "A2");
		//Then
		assertThat(conversationSyncData).hasSize(1);
		assertThat(conversationSyncData.get(0).getConversationDataList()).hasSize(1);
		assertThat(conversationSyncData.get(0).getConversationDataList().get(0))
				.isInstanceOf(ConversationMessage.class);
		ConversationMessage conversationMessage = (ConversationMessage) conversationSyncData.get(0)
				.getConversationDataList().get(0);
		assertThat(conversationMessage.getMessageReceipt()).isNull();
	}

	@Test
	public void saveConversationTest() throws ConversationNotFoundException, InvalidUserException, InvalidConversation {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();

		//When
		conversationRepository.saveConversation(conversation);
		Conversation savedConversation = conversationRepository.getConversationWithId("1");

		//Then
		assertThat(savedConversation).isNotNull();
		assertThat(savedConversation.getUserIds()).containsExactly(1l, 2l);
	}

	@Test
	public void getNonExistentConversation() {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		conversationRepository.flush();
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationRepository.getConversationWithId("1");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ConversationNotFoundException.class);
	}

	@Test
	public void getOnlyReceiptsInRangeTest() {
		//Given
		ConversationRepository conversationRepository = givenARepositoryWithAMessageWithReceipts();

		//When
		ConversationHistory conversationHistory = conversationRepository
				.getConversationHistory("1", new Range(0l, 2l), 1l, "A2");

		//Then
		assertThat(conversationHistory.getConversationDataList()).hasSize(1);
		assertThat(conversationHistory.getConversationDataList()).extracting("id").containsExactly("1");
		ConversationData conversationData = conversationHistory.getConversationDataList().get(0);
		ConversationTextMessage conversationTextMessage = (ConversationTextMessage) conversationData;

		assertThat(conversationTextMessage.getMessageReceipt().getReceipts()).hasSize(2);
		assertThat(conversationTextMessage.getMessageReceipt().getReceipts())
				.extracting(receipt -> receipt.getType().toString()).containsExactly("received", "read");
		assertThat(conversationTextMessage.getMessageReceipt().getReceipts())
				.extracting(IndividualMessageReceipt::getUser).containsOnly(2l);
		assertThat(conversationTextMessage.getMessageReceipt().getReceipts()).extracting("date")
				.containsOnly(new Date(2l), new Date(3l));
	}

	@Test
	public void testReadIsInRange() {
		//GIVEN
		ConversationRepository conversationRepository = givenARepositoryWithAMessageWithReceipts();

		//WHEN
		ConversationHistory conversationHistory = conversationRepository
				.getConversationHistory("1", new Range(2l, 3l), 1l, "A2");

		//THEN
		assertThat(conversationHistory.getConversationDataList()).hasSize(1);
		assertThat(conversationHistory.getConversationDataList()).extracting("id").containsExactly("1");
		ConversationData conversationData = conversationHistory.getConversationDataList().get(0);
		assertThat(conversationData).isInstanceOf(MessageReceipt.class);
		MessageReceipt messageReceipt = (MessageReceipt) conversationData;
		assertThat(messageReceipt.getReceipts()).extracting(receipt -> receipt.getType().toString())
				.containsExactly("read");
	}

	@Test
	public void testReceivedAndReadAreInRange() {
		//GIVEN
		ConversationRepository conversationRepository = givenARepositoryWithAMessageWithReceipts();

		//WHEN
		ConversationHistory conversationHistory2 = conversationRepository
				.getConversationHistory("1", new Range(1l, 3l), 1l, "A2");

		//THEN
		assertThat(conversationHistory2.getConversationDataList()).hasSize(1);
		assertThat(conversationHistory2.getConversationDataList()).extracting("id").containsExactly("1");
		ConversationData conversationData = conversationHistory2.getConversationDataList().get(0);
		assertThat(conversationData).isInstanceOf(MessageReceipt.class);
		MessageReceipt messageReceipt = (MessageReceipt) conversationData;
		assertThat(messageReceipt.getReceipts()).extracting(receipt -> receipt.getType().toString())
				.containsExactly("received", "read");
	}

	@Test
	public void testDontSendReceiptWhenMessageIsNotSynced() {
		//GIVEN
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = new MemoryConversationRepository(addressedMessageFactory, 10,
				1);
		saveConversation(conversationRepository);
		addMessageWithDate(conversationRepository, 1l);
		addMessageWithDate(conversationRepository, 2l);
		IndividualMessageReceipt receipt1 = getIndividualMessageReceipt(2l, 3l, new ReceivedType());
		conversationRepository.saveReceiptInMessage("1", "1", receipt1);

		//WHEN
		ConversationHistory conversationHistory = conversationRepository
				.getConversationHistory("1", new Range(0l, 3l), 1l, "A2");

		//THEN
		assertThat(conversationHistory.getConversationDataList()).hasSize(1);
		assertThat(conversationHistory.getConversationDataList()).extracting("id").containsExactly("2");
		ConversationData conversationData = conversationHistory.getConversationDataList().get(0);
		assertThat(conversationData).isInstanceOf(ConversationTextMessage.class);
	}

	private ConversationRepository givenARepositoryWithAMessageWithReceipts() {
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = new MemoryConversationRepository(addressedMessageFactory, 10,
				1);
		IndividualMessageReceipt receipt1 = getIndividualMessageReceipt(2l, 2l, new ReceivedType());
		IndividualMessageReceipt receipt2 = getIndividualMessageReceipt(2l, 3l, new ReadType());
		saveConversation(conversationRepository);
		addMessageWithDate(conversationRepository, 1l);
		conversationRepository.saveReceiptInMessage("1", "1", receipt1);
		conversationRepository.saveReceiptInMessage("1", "1", receipt2);
		return conversationRepository;
	}

	private IndividualMessageReceipt getIndividualMessageReceipt(long userId, long date, ReceiptType type) {
		IndividualMessageReceipt receipt = mock(IndividualMessageReceipt.class);
		when(receipt.getUser()).thenReturn(userId);
		when(receipt.getDate()).thenReturn(new Date(date));
		when(receipt.getType()).thenReturn(type);
		return receipt;
	}

	@Test
	public void saveMessageGetMessageTest()
			throws InvalidConversation, InvalidUserException, ConversationNotFoundException,
			UserNotInConversationException, InvalidMessageException, MessageNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		conversationRepository.flush();
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "blah",
				"A2", false);

		//When
		ConversationMessage savedMessage = conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();
		ConversationMessage retrievedMessage = conversationRepository.getConversationMessage("1", savedMessage.getId(), 1l);

		//Then
		assertThat(savedMessage).isNotNull();
		assertThat(savedMessage.getSender().getId()).isEqualTo(1l);
		assertThat(savedMessage.getConversationId()).isEqualTo("1");
		assertThat(savedMessage).isEqualToComparingFieldByField(retrievedMessage);
	}

	@Test
	public void getConversationSyncMessagesTest() throws ModelException, ConversationNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "blah",
				"A2", false);
		conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();
		//When
		List<ConversationSync> conversationSyncMessages = conversationRepository
				.getConversationSyncData(1l, Collections.singletonList("1"), new Date(1l), "A2");
		List<ConversationSync> conversationSyncMessagesUserTwo = conversationRepository
				.getConversationSyncData(2l, Collections.singletonList("1"), new Date(1l), "A2");

		//Then
		assertThat(conversationSyncMessages).hasSize(1);
		ConversationSync conversationSync = conversationSyncMessages.get(0);
		assertThat(conversationSync).isNotNull();
		assertThat(conversationSync.getUnreadMessages()).isEqualTo(0l);
		assertThat(conversationSync.getConversationId()).isEqualTo("1");
		assertThat(conversationSync.getConversationDataList()).hasSize(1);
		assertThat(conversationSync.getHasMore().getHasMore()).isEqualTo(false);
		assertThat(conversationSyncMessagesUserTwo).hasSize(1);
		assertThat(conversationSyncMessagesUserTwo.get(0).getUnreadMessages()).isEqualTo(1);
	}

	@Test
	public void testSyncDifferentApp(){
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "blah",
				"A2", false);
		conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();
		//When
		List<ConversationSync> conversationSyncMessages = conversationRepository
				.getConversationSyncData(1l, Collections.singletonList("1"), new Date(1l), "P2");
		//Then
		assertThat(conversationSyncMessages).isEmpty();
	}

	@Test
	public void resetReadTest() throws ModelException, ConversationNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "blah",
				"A2", false);
		conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();

		//When
		List<ConversationSync> conversationSyncMessagesUnread = conversationRepository
				.getConversationSyncData(2l, Collections.singletonList("1"), new Date(1l), "A2");

		conversationRepository.resetRead("1", "A2", 2l);
		conversationRepository.flush();
		List<ConversationSync> conversationSyncMessagesUserTwo = conversationRepository
				.getConversationSyncData(2l, Collections.singletonList("1"), new Date(1l), "A2");

		//Then
		assertThat(conversationSyncMessagesUserTwo).hasSize(1);
		assertThat(conversationSyncMessagesUserTwo.get(0).getUnreadMessages()).isEqualTo(0);
		assertThat(conversationSyncMessagesUnread.get(0).getUnreadMessages()).isEqualTo(1);
	}

	@Test
	public void resetReadDifferentAppTest() throws ModelException, ConversationNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "blah",
				"A2", false);
		conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();

		//When
		conversationRepository.resetRead("1", "P2", 2l);
		conversationRepository.flush();
		List<ConversationSync> conversationSyncMessagesUserTwo = conversationRepository
				.getConversationSyncData(2l, Collections.singletonList("1"), new Date(1l), "A2");

		//Then
		assertThat(conversationSyncMessagesUserTwo).hasSize(1);
		assertThat(conversationSyncMessagesUserTwo.get(0).getUnreadMessages()).isEqualTo(1);
	}

	@Test
	public void saveReadReceiptGetConversationSyncMessages() throws ModelException, ConversationNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "blah",
				"A2", false);
		ConversationMessage savedConversationMessage = conversationRepository.saveMessage(conversationMessage, conversation);
		IndividualMessageReceipt receipt = new IndividualMessageReceipt(new ReadType(), 2l);

		//When
		conversationRepository.saveReceiptInMessage("1", savedConversationMessage.getId(), receipt);
		conversationRepository.flush();
		List<ConversationSync> conversationSyncMessages = conversationRepository
				.getConversationSyncData(1l, Collections.singletonList("1"), new Date(2), "A2");

		//Then
		assertThat(conversationSyncMessages).hasSize(1);
		assertThat(conversationSyncMessages.get(0).getUnreadMessages()).isEqualTo(0);
	}

	@Test
	public void getConversationWithUsersNotFoundTest() {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Set<Long> userIds = Sets.newHashSet(1l, 2l);
		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> conversationRepository
				.getConversationWithUsers(userIds);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ConversationNotFoundException.class);
	}

	@Test
	public void saveConversationGetConversationWithUsersTest()
			throws InvalidConversation, InvalidUserException, ConversationNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Set<Long> userIds = Sets.newHashSet(1l, 2l);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		conversationRepository.flush();
		//When
		Conversation conversationWithUsers = conversationRepository.getConversationWithUsers(userIds);

		//Then
		assertThat(conversationWithUsers).isNotNull();
		assertThat(conversationWithUsers.getUserIds()).containsExactly(1l, 2l);
	}

	@Test
	public void deleteMessageGetHistoryTest()
			throws InvalidConversation, InvalidUserException, InvalidMessageException, ConversationNotFoundException,
			UserNotInConversationException, MessageNotFoundException, InvalidRangeException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "bla",
				"A2", false);
		ConversationMessage savedMessage = conversationRepository.saveMessage(conversationMessage, conversation);

		//When
		conversationRepository.deleteMessage("1", savedMessage.getId(), 1l);
		conversationRepository.flush();
		ConversationHistory conversationHistoryUserOne = conversationRepository
				.getConversationHistory("1", new Range(null, null), 1l, "A2");
		ConversationHistory conversationHistoryUserTwo = conversationRepository
				.getConversationHistory("1", new Range(null, null), 2l, "A2");

		//Then
		assertThat(conversationHistoryUserOne.getConversationDataList()).hasSize(0);
		assertThat(conversationHistoryUserTwo.getConversationDataList()).hasSize(1);
		ConversationData conversationData = conversationHistoryUserTwo.getConversationDataList().get(0);
		assertThat(conversationData.getType().equals("message"));
		ConversationMessage conversationMessage1 = (ConversationMessage) conversationData;
		assertThat(conversationMessage1.getSender().getId()).isEqualTo(1l);
		assertThat(conversationMessage1.getConversationId()).isEqualTo("1");
	}

	@Test
	public void deleteConversationHistoryTest()
			throws InvalidConversation, InvalidUserException, UserNotInConversationException,
			ConversationNotFoundException, InvalidMessageException, InvalidRangeException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		conversationRepository.flush();
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "bla",
				"A2", false);
		conversationRepository.saveMessage(conversationMessage, conversation);

		//When
		conversationRepository.deleteConversation("1", 1l, new Date(), "A2");
		conversationRepository.flush();
		ConversationHistory conversationHistoryUserOne = conversationRepository
				.getConversationHistory("1", new Range(null, null), 1l, "A2");
		ConversationHistory conversationHistoryUserTwo = conversationRepository
				.getConversationHistory("1", new Range(null, null), 2l, "A2");

		//Then
		assertThat(conversationHistoryUserOne.getConversationDataList()).hasSize(0);
		assertThat(conversationHistoryUserTwo.getConversationDataList()).hasSize(1);
	}

	@Test
	public void deleteMessageSyncTest() {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "bla",
				"A2", false);
		ConversationMessage savedConversationMessage = conversationRepository.saveMessage(conversationMessage,
				conversation);

		//When
		conversationRepository.deleteMessage("1", savedConversationMessage.getId(), 1l);
		conversationRepository.flush();
		List<ConversationSync> conversationSyncDataUserOne = conversationRepository
				.getConversationSyncData(1l, Collections.singletonList("1"), new Date(0), "A2");
		List<ConversationSync> conversationSyncDataUserTwo = conversationRepository
				.getConversationSyncData(2l, Collections.singletonList("1"), new Date(0), "A2");

		//Then
		assertThat(conversationSyncDataUserOne).isEmpty();
		assertThat(conversationSyncDataUserTwo).hasSize(1);
		List<ConversationData> conversationDataList = conversationSyncDataUserTwo.get(0).getConversationDataList();
		assertThat(conversationDataList).hasSize(1);
		assertThat(conversationDataList.get(0)).isEqualToComparingFieldByField(conversationMessage);
	}

	@Test
	public void deleteConversationSyncTest() {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "bla",
				"A2", false);
		conversationRepository.saveMessage(conversationMessage, conversation);

		//When
		conversationRepository.deleteConversation("1", 1l, new Date(), "A2");
		conversationRepository.flush();
		List<ConversationSync> conversationSyncDataUserOne = conversationRepository
				.getConversationSyncData(1l, Collections.singletonList("1"), new Date(0), "A2");
		List<ConversationSync> conversationSyncDataUserTwo = conversationRepository
				.getConversationSyncData(2l, Collections.singletonList("1"), new Date(0), "A2");

		//Then
		assertThat(conversationSyncDataUserOne).isEmpty();
		assertThat(conversationSyncDataUserTwo).hasSize(1);
		List<ConversationData> conversationDataList = conversationSyncDataUserTwo.get(0).getConversationDataList();
		assertThat(conversationDataList).hasSize(1);
		assertThat(conversationDataList.get(0)).isEqualToComparingFieldByField(conversationMessage);
	}

	@Test
	public void getAddressedMessagesTest()
			throws InvalidConversation, InvalidUserException, InvalidMessageException, ConversationNotFoundException,
			UserNotInConversationException, MessageNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = new AddressedMessageFactory();
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "bla",
				"A2", false);
		conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();

		//When
		List<AddressedMessage> addressedMessages = conversationRepository
				.getAddressedMessages(Arrays.asList(1l, 2l), new Date(1), "A2");

		//Then
		assertThat(addressedMessages).hasSize(1);
		AddressedMessage addressedMessage = addressedMessages.get(0);
		assertThat(addressedMessage.getText()).isEqualTo("bla");
		assertThat(addressedMessage.getUser().getId()).isEqualTo(2l);
		assertThat(addressedMessage.getSender().getId()).isEqualTo(1l);
	}

	@Test
	public void saveImageGetImageTest() throws InvalidConversation, InvalidUserException, ConversationNotFoundException,
			UserNotInConversationException, InvalidMessageException, MessageNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		conversationRepository.flush();
		ConversationMessage conversationMessage = new ConversationImageMessage(new User(1l), "1", new Date(2l), "url", "thumbnail",
				"format", "orientation", "A2", false);

		//When
		ConversationMessage savedMessage = conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();
		ConversationMessage retrievedMessage = conversationRepository.getConversationMessage("1", savedMessage.getId(), 1l);

		//Then
		assertThat(savedMessage).isNotNull();
		assertThat(savedMessage.getSender().getId()).isEqualTo(1l);
		assertThat(savedMessage.getConversationId()).isEqualTo("1");
		assertThat(savedMessage).isEqualToComparingFieldByField(retrievedMessage);
		ConversationImageMessage conversationImageMessage = (ConversationImageMessage) savedMessage;
		assertThat(conversationImageMessage.getApplication()).isEqualTo("A2");
		assertThat(conversationImageMessage.getFormat()).isEqualTo("format");
		assertThat(conversationImageMessage.getOrientation()).isEqualTo("orientation");
		assertThat(conversationImageMessage.getThumbnail()).isEqualTo("thumbnail");
		assertThat(conversationImageMessage.getUrl()).isEqualTo("url");
	}

	@Test
	public void saveVideoGetVideoTest() throws InvalidConversation, InvalidUserException, ConversationNotFoundException,
			UserNotInConversationException, InvalidMessageException, MessageNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		conversationRepository.flush();
		ConversationMessage conversationMessage = new ConversationVideoMessage(new User(1l), "1", new Date(2l), "url", "thumbnail", 2l,
				"format", "orientation", "A2", false);

		//When
		ConversationMessage savedMessage = conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();
		ConversationMessage retrievedMessage = conversationRepository.getConversationMessage("1", savedMessage.getId(), 1l);

		//Then
		assertThat(savedMessage).isNotNull();
		assertThat(savedMessage.getSender().getId()).isEqualTo(1l);
		assertThat(savedMessage.getConversationId()).isEqualTo("1");
		assertThat(savedMessage).isEqualToComparingFieldByField(retrievedMessage);
		ConversationVideoMessage conversationVideoMessage = (ConversationVideoMessage) savedMessage;
		assertThat(conversationVideoMessage.getApplication()).isEqualTo("A2");
		assertThat(conversationVideoMessage.getFormat()).isEqualTo("format");
		assertThat(conversationVideoMessage.getOrientation()).isEqualTo("orientation");
		assertThat(conversationVideoMessage.getThumbnail()).isEqualTo("thumbnail");
		assertThat(conversationVideoMessage.getUrl()).isEqualTo("url");
		assertThat(conversationVideoMessage.getLength()).isEqualTo(2l);
	}

	@Test
	public void saveAudioGetAudioTest() throws InvalidConversation, InvalidUserException, ConversationNotFoundException,
			UserNotInConversationException, InvalidMessageException, MessageNotFoundException {
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		conversationRepository.flush();
		ConversationMessage conversationMessage = new ConversationAudioMessage(new User(1l), "1", new Date(2l), "url", 2l,
				"format", "A2", false);

		//When
		ConversationMessage savedMessage = conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();
		ConversationMessage retrievedMessage = conversationRepository.getConversationMessage("1", savedMessage.getId(), 1l);

		//Then
		assertThat(savedMessage).isNotNull();
		assertThat(savedMessage.getSender().getId()).isEqualTo(1l);
		assertThat(savedMessage.getConversationId()).isEqualTo("1");
		assertThat(savedMessage).isEqualToComparingFieldByField(retrievedMessage);
		ConversationAudioMessage conversationAudioMessage = (ConversationAudioMessage) savedMessage;
		assertThat(conversationAudioMessage.getApplication()).isEqualTo("A2");
		assertThat(conversationAudioMessage.getFormat()).isEqualTo("format");
		assertThat(conversationAudioMessage.getUrl()).isEqualTo("url");
		assertThat(conversationAudioMessage.getLength()).isEqualTo(2l);
	}

	@Test
	public void testHistoryDifferentApp(){
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		conversationRepository.flush();
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(1l), "blah",
				"A2", false);


		conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();

		//When
		ConversationHistory conversationHistory = conversationRepository
				.getConversationHistory("1", new Range(0l, 2l), 2l, "P2");
		//Then
		assertThat(conversationHistory.getConversationDataList()).isEmpty();

	}

	@Test
	public void deleteConversationDifferentApp(){
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		conversationRepository.flush();
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "bla",
				"A2", false);
		conversationRepository.saveMessage(conversationMessage, conversation);

		//When
		conversationRepository.deleteConversation("1", 1l, new Date(), "P2");
		conversationRepository.flush();
		ConversationHistory conversationHistoryUserOne = conversationRepository
				.getConversationHistory("1", new Range(null, null), 1l, "A2");

		//Then
		assertThat(conversationHistoryUserOne.getConversationDataList()).hasSize(1);
	}

	@Test
	public void alreadyAcknowledgedTest(){
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		IndividualMessageReceipt receipt = new IndividualMessageReceipt(new ReceivedType(), 2l);
		IndividualMessageReceipt anotherReceipt = new IndividualMessageReceipt(new ReceivedType(), 2l);
		saveConversation(conversationRepository);
		ConversationMessage conversationMessage = addMessageWithDate(conversationRepository, 1l);
		conversationRepository.saveReceiptInMessage("1", conversationMessage.getId(), receipt);
		conversationRepository.flush();

		//When
		boolean alreadyAcknowledged = conversationRepository
				.isAlreadyAcknowledged("1", conversationMessage.getId(), anotherReceipt);

		//Then
		assertThat(alreadyAcknowledged);
	}

	@Test
	public void notAcknowledgedTest(){
		//Given
		AddressedMessageFactory addressedMessageFactory = mock(AddressedMessageFactory.class);
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		IndividualMessageReceipt receipt = new IndividualMessageReceipt(new ReceivedType(), 2l);
		IndividualMessageReceipt anotherReceipt = new IndividualMessageReceipt(new ReadType(), 2l);
		saveConversation(conversationRepository);
		ConversationMessage conversationMessage = addMessageWithDate(conversationRepository, 1l);
		conversationRepository.saveReceiptInMessage("1", conversationMessage.getId(), receipt);
		conversationRepository.flush();

		//When
		boolean alreadyAcknowledged = conversationRepository
				.isAlreadyAcknowledged("1", conversationMessage.getId(), anotherReceipt);

		//Then
		assertThat(alreadyAcknowledged).isEqualTo(false);
	}

	@Test
	public void saveAndGetIgnoredMessage(){
		//Given
		AddressedMessageFactory addressedMessageFactory = new AddressedMessageFactory();
		ConversationRepository conversationRepository = givenAConversationRepository(addressedMessageFactory);
		Conversation conversation = Given.givenAConversationWithTwoUsers();
		conversationRepository.saveConversation(conversation);
		ConversationMessage conversationMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "bla",
				"A2", true);
		conversationRepository.saveMessage(conversationMessage, conversation);
		conversationRepository.flush();

		//When
		List<AddressedMessage> addressedMessages = conversationRepository
				.getAddressedMessages(Arrays.asList(2l, 1l), new Date(1), "A2");

		//Then
		assertThat(addressedMessages).hasSize(0);
	}

}
