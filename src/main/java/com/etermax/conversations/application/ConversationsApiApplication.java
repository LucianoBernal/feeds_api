package com.etermax.conversations.application;

import com.codahale.metrics.health.HealthCheck;
import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.adapter.ReceiptAdapter;
import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.application.config.AppConfiguration;
import com.etermax.conversations.application.healthcheck.factory.ConversationRepositoryHealthCheckFactory;
import com.etermax.conversations.dto.AppInfoDTO;
import com.etermax.conversations.factory.*;
import com.etermax.conversations.metrics.MetricsPublisher;
import com.etermax.conversations.metrics.configuration.MetricsConfiguration;
import com.etermax.conversations.resource.*;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.MessageService;
import com.etermax.jvon.core.JvonPreProccessor;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConversationsApiApplication extends Application<AppConfiguration> {

	private static final Logger logger = LoggerFactory.getLogger(ConversationsApiApplication.class);
	
	public static void main(String[] args) throws Exception {
		new ConversationsApiApplication().run(args);
	}

	@Override
	public void run(AppConfiguration configuration, Environment environment) throws Exception {
		JvonPreProccessor.scan("com.etermax");
		logger.info("Starting Conversations application using configuration {}", configuration);
		ConversationAdapter conversationAdapter = createConversationAdapter(configuration);
		environment.jersey().register(new ApplicationResource());
		environment.jersey().register(new ConversationsResource(conversationAdapter));
		environment.jersey().register(new ConversationsQueryResource(conversationAdapter));
		environment.jersey().register(new ConversationResource(conversationAdapter));
		environment.jersey().register(new ConversationDeletionResource(conversationAdapter));

		MessageAdapter messageAdapter = createMessageAdapter(configuration);
		SynchronizationAdapter synchronizationAdapter = createSyncronizationAdapter(configuration);

		MetricsConfiguration metricsConfiguration = configuration.getMetricsConfiguration();
		MetricsPublisher metricsPublisher = metricsConfiguration.createMetricsPublisher();

		ConversationService conversationService = createConversationServiceFactory(configuration)
				.createConversationService();

		MessageService messageService = createMessageService(configuration);

		environment.jersey().register(new ConversationMessagesResource(messageAdapter, metricsPublisher));
		environment.jersey().register(new ConversationMessageDeletionResource(messageAdapter));

		environment.jersey().register(new SyncResource(synchronizationAdapter));
		environment.jersey().register(new HistoryResource(synchronizationAdapter));
		ReceiptAdapter receiptAdapter = createReceiptAdapter(configuration);
		environment.jersey().register(new MessageReceiptsResource(receiptAdapter));

		environment.getApplicationContext().addServlet(HystrixMetricsStreamServlet.class, "/eternoc/hystrix.stream");

		registerHealthChecks(configuration, environment);
		logger.info("Conversations api started");
	}

	private void registerHealthChecks(AppConfiguration configuration, Environment environment) {
		ConversationRepositoryHealthCheckFactory conversationRepositoryHealthCheckFactory = configuration
				.getConversationRepositoryFactory().createRepositoryHealthCheckFactory();
		HealthCheck repositoryHealthcheck = conversationRepositoryHealthCheckFactory.createRepositoryHealthcheck();
		environment.healthChecks().register("ConversationRepository", repositoryHealthcheck);
		HealthCheck counterHealthCheck = conversationRepositoryHealthCheckFactory.createCounterHealthcheck();
		environment.healthChecks().register("Counter", counterHealthCheck);
	}

	private MessageService createMessageService(AppConfiguration configuration) {
		ConversationRepositoryFactory conversationRepositoryFactory = configuration.getConversationRepositoryFactory();
		ConversationMessageFactory conversationMessageFactory = new ConversationMessageFactory();
		ConversationServiceFactory conversationServiceFactory = createConversationServiceFactory(configuration);
		EventServiceFactory eventServiceFactory = new EventServiceFactory(conversationRepositoryFactory);
		MessageServiceFactory messageServiceFactory = createMessageServiceFactory(
				conversationRepositoryFactory, conversationMessageFactory, conversationServiceFactory,
				eventServiceFactory);
		return messageServiceFactory.createMessageService();
	}

	private SynchronizationAdapter createSyncronizationAdapter(AppConfiguration configuration) {
		ConversationRepositoryFactory conversationRepositoryFactory = configuration.getConversationRepositoryFactory();
		SynchronizationServiceFactory synchronizationServiceFactory = new SynchronizationServiceFactory(
				conversationRepositoryFactory);
		SynchronizationAdapterFactory synchronizationAdapterFactory = new SynchronizationAdapterFactory(
				synchronizationServiceFactory);
		return synchronizationAdapterFactory.createSyncronizationAdapter();
	}

	private MessageAdapter createMessageAdapter(AppConfiguration configuration) {
		ConversationRepositoryFactory conversationRepositoryFactory = configuration.getConversationRepositoryFactory();
		ConversationMessageFactory conversationMessageFactory = new ConversationMessageFactory();
		UserFactory userFactory = new UserFactory();
		ConversationServiceFactory conversationServiceFactory = createConversationServiceFactory(configuration);
		EventServiceFactory eventServiceFactory = new EventServiceFactory(conversationRepositoryFactory);
		MessageServiceFactory messageServiceFactory = createMessageServiceFactory(
				conversationRepositoryFactory, conversationMessageFactory, conversationServiceFactory,
				eventServiceFactory);
		MessageAdapterFactory messageAdapterFactory = new MessageAdapterFactory(messageServiceFactory,
				conversationMessageFactory, userFactory);
		return messageAdapterFactory.createMessageAdapter();
	}

	private MessageServiceFactory createMessageServiceFactory(ConversationRepositoryFactory conversationRepositoryFactory,
			ConversationMessageFactory conversationMessageFactory,
			ConversationServiceFactory conversationServiceFactory, EventServiceFactory eventServiceFactory) {
		return new MessageServiceFactory(conversationMessageFactory, conversationRepositoryFactory,
				conversationServiceFactory, eventServiceFactory);
	}

	private ReceiptAdapter createReceiptAdapter(AppConfiguration configuration) {
		IndividualMessageReceiptFactory individualMessageReceiptFactory = new IndividualMessageReceiptFactory();
		ConversationRepositoryFactory conversationRepositoryFactory = configuration.getConversationRepositoryFactory();
		ReceiptServiceFactory receiptServiceFactory = new ReceiptServiceFactory(conversationRepositoryFactory);
		ReceiptAdapterFactory receiptAdapterFactory = new ReceiptAdapterFactory(receiptServiceFactory,
				individualMessageReceiptFactory);
		return receiptAdapterFactory.createReceiptAdapter();
	}

	private ConversationAdapter createConversationAdapter(AppConfiguration configuration) {
		ConversationServiceFactory conversationServiceFactory = createConversationServiceFactory(configuration);
		ConversationAdapterFactory adapterFactory = new ConversationAdapterFactory(conversationServiceFactory);
		return adapterFactory.createAdapter();
	}

	private ConversationServiceFactory createConversationServiceFactory(AppConfiguration configuration) {
		ConversationFactory conversationFactory = new ConversationFactory();
		UserFactory userFactory = new UserFactory();
		ConversationRepositoryFactory conversationRepositoryFactory = configuration.getConversationRepositoryFactory();
		return new ConversationServiceFactory(conversationRepositoryFactory, conversationFactory, userFactory,
				new EventServiceFactory(conversationRepositoryFactory));
	}

	@Override
	public void initialize(Bootstrap<AppConfiguration> bootstrap) {
		bootstrap.addBundle(new SwaggerBundle<Configuration>() {
			@Override
			protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(Configuration configuration) {
				SwaggerBundleConfiguration swaggerConfig = new SwaggerBundleConfiguration();
				swaggerConfig.setResourcePackage("com.etermax.conversations.resource");
				swaggerConfig.setTitle("Conversations Api");
				swaggerConfig.setDescription("Conversation Management Service");
				swaggerConfig.setVersion(AppInfoDTO.getInstance().getVersion());
				return swaggerConfig;
			}
		});
		super.initialize(bootstrap);
	}
}
