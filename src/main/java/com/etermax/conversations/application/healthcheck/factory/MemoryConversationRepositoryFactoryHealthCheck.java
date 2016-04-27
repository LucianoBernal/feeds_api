package com.etermax.conversations.application.healthcheck.factory;

import com.codahale.metrics.health.HealthCheck;
import com.etermax.conversations.application.healthcheck.EmptyHealthCheck;

public class MemoryConversationRepositoryFactoryHealthCheck implements ConversationRepositoryHealthCheckFactory {
	@Override
	public HealthCheck createRepositoryHealthcheck() {
		return new EmptyHealthCheck();
	}

	@Override
	public HealthCheck createCounterHealthcheck() {
		return new EmptyHealthCheck();
	}
}
