package com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory.impl;

import com.codahale.metrics.health.HealthCheck;
import com.etermax.conversations.application.healthcheck.RedisCounterHealthCheck;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.CounterDAO;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory.CounterDAOFactory;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.impl.RedisCounterDAO;
import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.etermax.conversations.retrocompatibility.migration.repository.impl.RedisMigrationRepository;
import com.etermax.jvon.core.JvonPreProccessor;
import com.etermax.vedis.connection.VedisConnectionConfiguration;
import com.etermax.vedis.connection.VedisConnectionManager;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;
import com.etermax.vedis.executor.SingleOperationExecutor;
import com.etermax.vedis.executor.bulk.BulkExecutor;
import com.fasterxml.jackson.annotation.JsonProperty;
import jersey.repackaged.com.google.common.collect.Lists;

public class RedisCounterDAOFactory implements CounterDAOFactory{

	@JsonProperty
	private String servers;
	@JsonProperty
	private String staticServer;
	@JsonProperty
	private String password;
	@JsonProperty
	private Integer timeBetweenEvictionRunsMillis = 3000;
	@JsonProperty
	private Long minEvictableIdleTimeMillis = 600000L;
	@JsonProperty
	private Integer minIdle = 2;
	@JsonProperty
	private Integer maxIdle = -1;
	@JsonProperty
	private Integer maxActive = 70;
	@JsonProperty
	private Integer maxWait = 10000;
	@JsonProperty
	private Boolean testOnBorrow = false;
	@JsonProperty
	private Boolean testWhileIdle = true;
	@JsonProperty
	private Integer timeout = 0;
	@JsonProperty
	private Integer whenExhaustedAction = 1;
	@JsonProperty ("daysThreshold")
	private Long daysThreshold = 31l;

	@Override
	public CounterDAO createDAO() {
		try {
			VedisConnectionManager manager = new VedisConnectionManager(createConfiguration());
			return new RedisCounterDAO(new SingleOperationExecutor(manager), new BulkExecutor(manager));
		} catch (InvalidConnectionDataException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public HealthCheck createCounterHealthCheck() throws InvalidConnectionDataException {
		return new RedisCounterHealthCheck(createConfiguration());
	}

	public VedisConnectionConfiguration createConfiguration() {
		VedisConnectionConfiguration config = new VedisConnectionConfiguration();
		config.setServers(Lists.newArrayList(servers.split(",")));
		config.setIsBlockedkWhenExhausted(whenExhaustedAction == 1);
		config.setMaxActive(maxActive);
		config.setMaxIdle(maxIdle);
		config.setMaxWait(maxWait);
		config.setPassword(password);
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		config.setMinIdle(minIdle);
		config.setStaticServer(staticServer);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestWhileIdle(testWhileIdle);
		config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		config.setTimeOut(timeout);
		return config;
	}

}
