package com.etermax.conversations.application.healthcheck.factory;

import com.codahale.metrics.health.HealthCheck;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;

public interface ConversationRepositoryHealthCheckFactory {
	HealthCheck createRepositoryHealthcheck();
	HealthCheck createCounterHealthcheck();
}
