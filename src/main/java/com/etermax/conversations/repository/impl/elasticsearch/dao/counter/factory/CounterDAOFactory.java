package com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory;

import com.codahale.metrics.health.HealthCheck;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.CounterDAO;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory.impl.MemoryCounterDAOFactory;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory.impl.RedisCounterDAOFactory;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value=RedisCounterDAOFactory.class, name="redis"),
		@JsonSubTypes.Type(value=MemoryCounterDAOFactory.class, name="memory")
})
public interface CounterDAOFactory {

	CounterDAO createDAO();
	HealthCheck createCounterHealthCheck() throws InvalidConnectionDataException;

}
