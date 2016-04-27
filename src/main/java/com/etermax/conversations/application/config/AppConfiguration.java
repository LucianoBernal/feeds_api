package com.etermax.conversations.application.config;

import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.metrics.configuration.GraphiteMetricsConfiguration;
import com.etermax.conversations.metrics.configuration.MetricsConfiguration;
import com.etermax.conversations.notification.sender.factory.NotificationSenderFactory;
import com.etermax.conversations.retrocompatibility.factory.RetrocompatibilityMessageServiceFactory;
import com.etermax.conversations.retrocompatibility.migration.repository.factory.MigrationRepositoryFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AppConfiguration extends Configuration {
	@JsonProperty("conversation_repository")
	@NotNull
	@Valid
	private ConversationRepositoryFactory conversationRepositoryFactory;

	@JsonProperty("migration")
	@NotNull
	@Valid
	private MigrationRepositoryFactory migrationRepositoryFactory;

	@JsonProperty("notifications")
	@NotNull
	@Valid
	private NotificationSenderFactory notificationSenderFactory;

	@JsonProperty("users_api_url")
	@NotNull
	@Valid
	private String usersApiUrl;

	@JsonProperty("retrocompatibility_message_service")
	@NotNull
	@Valid
	private RetrocompatibilityMessageServiceFactory retrocompatibilityMessageServiceFactory;

	@JsonProperty("metricsReporter")
	@NotNull
	@Valid
	private MetricsConfiguration metricsConfiguration;

	public ConversationRepositoryFactory getConversationRepositoryFactory() {
		return conversationRepositoryFactory;
	}

	public MigrationRepositoryFactory getMigrationRepositoryFactory() {
		return migrationRepositoryFactory;
	}

	public NotificationSenderFactory getNotificationSenderFactory() {
		return notificationSenderFactory;
	}

	public String getUsersApiUrl() {
		return usersApiUrl;
	}

	public RetrocompatibilityMessageServiceFactory getRetrocompatibilityMessageServiceFactory() {
		return retrocompatibilityMessageServiceFactory;
	}

	public MetricsConfiguration getMetricsConfiguration() {	return metricsConfiguration; }

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
