package com.etermax.conversations.factory;

import com.etermax.conversations.application.healthcheck.factory.ConversationRepositoryHealthCheckFactory;
import com.etermax.conversations.repository.ConversationRepository;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value=ElasticSearchRepositoryFactory.class, name="elasticsearch"),
		@JsonSubTypes.Type(value=MemoryConversationRepositoryFactory.class, name="memory")
})
public interface ConversationRepositoryFactory extends Discoverable {
	ConversationRepository createRepository();
	ConversationRepositoryHealthCheckFactory createRepositoryHealthCheckFactory();
}
