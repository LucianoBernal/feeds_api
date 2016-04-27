package com.etermax.conversations.test.integration;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.ReceiptAdapter;
import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.application.healthcheck.factory.ConversationRepositoryHealthCheckFactory;
import com.etermax.conversations.dto.TextMessageCreationDTO;
import com.etermax.conversations.error.InvalidConversation;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.factory.*;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationComparator;
import com.etermax.conversations.model.User;
import com.etermax.conversations.notification.sender.factory.NotificationSenderFactory;
import com.etermax.conversations.notification.sender.factory.impl.NoneNotificationSenderFactory;
import com.etermax.conversations.notification.service.NotificationService;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.repository.impl.elasticsearch.ElasticsearchConversationRepository;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.impl.MemoryCounterDAO;
import com.etermax.conversations.repository.impl.elasticsearch.strategy.CounterConversationIdGenerator;
import com.etermax.conversations.repository.impl.memory.MemoryConversationRepository;
import com.etermax.conversations.retrocompatibility.migration.service.MigrationService;
import com.etermax.conversations.retrocompatibility.service.DisabledRetrocompatibilityMessageService;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityMessageService;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.EventService;
import com.etermax.conversations.service.impl.ConversationServiceImpl;
import com.etermax.conversations.service.impl.EventServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Given {

	public static ConversationRepository repository;

	private static ConversationAdapterFactory givenAConversationAdapterFactory(
			ConversationRepositoryFactory conversationRepositoryFactory) {
		ConversationFactory conversationFactory = new ConversationFactory();
		UserFactory userFactory = new UserFactory();
		EventServiceFactory eventServiceFactory = new EventServiceFactory(conversationRepositoryFactory);
		NotificationServiceFactory notificationServiceFactory = new NotificationServiceFactory(
				new NoneNotificationSenderFactory());
		ConversationServiceFactory conversationServiceFactory = new ConversationServiceFactory(
				conversationRepositoryFactory, conversationFactory, userFactory, eventServiceFactory,
				notificationServiceFactory);
		return new ConversationAdapterFactory(conversationServiceFactory);
	}

	public static ReceiptAdapter givenAReceiptAdapter(ConversationRepositoryFactory conversationRepositoryFactory) {
		IndividualMessageReceiptFactory individualMessageReceiptFactory = new IndividualMessageReceiptFactory();
		ReceiptServiceFactory receiptServiceFactory = new ReceiptServiceFactory(conversationRepositoryFactory);
		ReceiptAdapterFactory receiptAdapterFactory = new ReceiptAdapterFactory(receiptServiceFactory,
																				individualMessageReceiptFactory);
		return receiptAdapterFactory.createReceiptAdapter();
	}

	public static TextMessageCreationDTO givenATextConversationMessageCreationDTO(String text) {
		TextMessageCreationDTO textCreationDTO = new TextMessageCreationDTO();
		textCreationDTO.setSenderId(1l);
		textCreationDTO.setText(text);
		textCreationDTO.setApplication("A2");
		return textCreationDTO;
	}

	public static ConversationRepositoryFactory givenAConversationRepositoryFactory() {
		//integration and repository tests
		return givenAMemoryConversationRepositoryFactory();
//		return givenAnElasticConversationRepositoryFactory();
	}

	public static ConversationRepositoryFactory givenAMemoryConversationRepositoryFactory() {
		AddressedMessageFactory addressedMessageFactory = new AddressedMessageFactory();
		return new MemoryConversationRepositoryFactory(addressedMessageFactory, 10, 2);
	}

	public static ConversationRepositoryFactory givenAnElasticConversationRepositoryFactory() {
		ConversationRepositoryFactory elasticSearchRepositoryFactory = new ConversationRepositoryFactory() {
			public ConversationRepository conversationRepository;

			@Override
			public ConversationRepository createRepository() {
				if (conversationRepository == null) {
					conversationRepository = Given.givenAnElasticSearchConversationRepository(new AddressedMessageFactory());
				}
				return conversationRepository;
			}

			@Override
			public ConversationRepositoryHealthCheckFactory createRepositoryHealthCheckFactory() {
				return null;
			}
		};
		ConversationRepository repository = elasticSearchRepositoryFactory.createRepository();
		repository.clearRepository();
		return elasticSearchRepositoryFactory;
	}

	public static ConversationAdapter givenAConversationAdapter(
			ConversationRepositoryFactory conversationRepositoryFactory) {
		return givenAConversationAdapterFactory(conversationRepositoryFactory).createAdapter();
	}

	public static MessageAdapterFactory givenAMessageAdapterFactory(
			ConversationRepositoryFactory conversationRepositoryFactory) {
		ConversationMessageFactory conversationMessageFactory = new ConversationMessageFactory();
		AddressedMessageFactory addressedMessageFactory = new AddressedMessageFactory();
		UserFactory userFactory = new UserFactory();
		ConversationFactory conversationFactory = new ConversationFactory();
		NotificationServiceFactory notificationServiceFactory = new NotificationServiceFactory(
				new NoneNotificationSenderFactory());
		ConversationServiceFactory conversationServiceFactory = new ConversationServiceFactory(
				conversationRepositoryFactory, conversationFactory, userFactory,
				givenAnEventServiceFactory(conversationRepositoryFactory), notificationServiceFactory);
		RetrocompatibilityMessageService retrocompatibilityMessageService = new DisabledRetrocompatibilityMessageService();
		MessageServiceFactory messageServiceFactory = new MessageServiceFactory(conversationMessageFactory,
																				conversationRepositoryFactory,
																				conversationServiceFactory,
																				givenAnEventServiceFactory(
																						conversationRepositoryFactory),
																				new NotificationServiceFactory(
																						new
																								NoneNotificationSenderFactory()),
				retrocompatibilityMessageService);
		return new MessageAdapterFactory(messageServiceFactory, conversationMessageFactory, addressedMessageFactory,
										 userFactory);
	}

	public static EventServiceFactory givenAnEventServiceFactory(
			ConversationRepositoryFactory ConversationRepositoryFactory) {
		return new EventServiceFactory(ConversationRepositoryFactory);
	}

	public static MessageAdapter givenAMessageAdapter(ConversationRepositoryFactory conversationRepositoryFactory) {
		return givenAMessageAdapterFactory(conversationRepositoryFactory).createMessageAdapter();
	}

	public static SynchronizationAdapterFactory givenASynchronizationAdapterFactory(
			ConversationRepositoryFactory conversationRepositoryFactory) {
		SynchronizationServiceFactory synchronizationServiceFactory = new SynchronizationServiceFactory(
				conversationRepositoryFactory);
		return new SynchronizationAdapterFactory(synchronizationServiceFactory, givenAMigrationService());
	}

	public static MigrationService givenAMigrationService() {
		MigrationService migrationService = mock(MigrationService.class);
		when(migrationService.migrateConversations(anyLong())).thenReturn(new ArrayList<>());
		return migrationService;
	}

	public static SynchronizationAdapter givenASynchronizationAdapter(
			ConversationRepositoryFactory conversationRepositoryFactory) {
		return givenASynchronizationAdapterFactory(conversationRepositoryFactory).createSyncronizationAdapter();
	}

	public static Conversation givenAConversationWithTwoUsers() throws InvalidUserException, InvalidConversation {
		Conversation conversation = new Conversation(Sets.newHashSet(new User(1l), new User(2l)));
		conversation.setId("1");
		return conversation;
	}

	public static ConversationRepository givenAMemoryConversationRepository(
			AddressedMessageFactory addressedMessageFactory) {
		return new MemoryConversationRepository(addressedMessageFactory, 10, 2);
//		return givenAnElasticSearchConversationRepository(addressedMessageFactory);
	}

	public static ConversationRepository givenAnElasticSearchConversationRepository(
			AddressedMessageFactory addressedMessageFactory) {

		if(repository == null){
			List<String> hosts = Lists.newArrayList("localhost:9300");
			repository = new
					ElasticsearchConversationRepository(
					initClient("crackme", hosts), 2, addressedMessageFactory,
					new CounterConversationIdGenerator("crackme", "localhost", 9300), new MemoryCounterDAO());
		}

		repository.clearRepository();
		return repository;
	}

	private static Client initClient(String cluster, List<String> hosts) {

		List<InetSocketTransportAddress> hostList = hosts.stream()
				.map(hostString -> new InetSocketTransportAddress(
						new InetSocketAddress(getHostname(hostString),
								getPort(hostString))))
				.collect(Collectors.toList());

		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", cluster)
				.put("client.transport.sniff", true)
				.build();

		return TransportClient.builder()
				.settings(settings)
				.build()
				.addTransportAddresses(hostList.toArray(new InetSocketTransportAddress[hostList.size()
						]));
	}

	private static int getPort(String hostPort) {
		return Integer.valueOf(hostPort.split(":")[1]);
	}

	private static String getHostname(String hostPort) {
		return hostPort.split(":")[0];
	}

	public static void flushRepository(ConversationRepositoryFactory conversationRepositoryFactory) {
		ConversationRepository repository = conversationRepositoryFactory.createRepository();
		repository.flush();
	}

	public static ConversationService givenAConversationService() {
		UserFactory userFactory = new UserFactory();
		ConversationComparator conversationComparator = new ConversationComparator();
		ConversationFactory conversationFactory = new ConversationFactory();
		ConversationRepository conversationRepository = givenAConversationRepositoryFactory().createRepository();
		EventService eventService = new EventServiceImpl(conversationRepository);
		NotificationSenderFactory senderFactory = new NoneNotificationSenderFactory();
		NotificationServiceFactory notificationServiceFactory = new NotificationServiceFactory(senderFactory);
		NotificationService notificationService = notificationServiceFactory.createNotificationService();
		return new ConversationServiceImpl(conversationRepository, conversationFactory, conversationComparator,
										   userFactory, eventService, notificationService);
	}
}
