package com.etermax.conversations.application.healthcheck.factory;

import com.codahale.metrics.health.HealthCheck;

public interface ConversationRepositoryHealthCheckFactory {
	HealthCheck createRepositoryHealthcheck();
	HealthCheck createCounterHealthcheck();
}
