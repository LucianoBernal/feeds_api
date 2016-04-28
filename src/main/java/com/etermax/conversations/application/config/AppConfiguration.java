package com.etermax.conversations.application.config;

import com.etermax.conversations.factory.ConversationRepositoryFactory;
import com.etermax.conversations.metrics.configuration.MetricsConfiguration;
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

	@JsonProperty("users_api_url")
	@NotNull
	@Valid
	private String usersApiUrl;

	@JsonProperty("metricsReporter")
	@NotNull
	@Valid
	private MetricsConfiguration metricsConfiguration;

	public ConversationRepositoryFactory getConversationRepositoryFactory() {
		return conversationRepositoryFactory;
	}


	public String getUsersApiUrl() {
		return usersApiUrl;
	}

	public MetricsConfiguration getMetricsConfiguration() {	return metricsConfiguration; }

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
