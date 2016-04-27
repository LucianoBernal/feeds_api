package com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory.impl;

import com.codahale.metrics.health.HealthCheck;
import com.etermax.conversations.application.healthcheck.EmptyHealthCheck;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.CounterDAO;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory.CounterDAOFactory;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.impl.MemoryCounterDAO;

public class MemoryCounterDAOFactory implements CounterDAOFactory{
	@Override
	public CounterDAO createDAO() {
		return new MemoryCounterDAO();
	}

	@Override
	public HealthCheck createCounterHealthCheck() {
		return new EmptyHealthCheck();
	}
}
