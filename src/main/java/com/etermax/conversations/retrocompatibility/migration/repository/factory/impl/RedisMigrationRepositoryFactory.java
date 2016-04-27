package com.etermax.conversations.retrocompatibility.migration.repository.factory.impl;

import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.etermax.conversations.retrocompatibility.migration.repository.factory.MigrationRepositoryFactory;
import com.etermax.conversations.retrocompatibility.migration.repository.impl.RedisMigrationRepository;
import com.etermax.jvon.core.JvonPreProccessor;
import com.etermax.vedis.connection.VedisConnectionConfiguration;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;
import com.fasterxml.jackson.annotation.JsonProperty;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RedisMigrationRepositoryFactory implements MigrationRepositoryFactory {

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
	public MigrationRepository createRepository() {
		try {
			JvonPreProccessor.scan("com.etermax");
			return new RedisMigrationRepository(createConfiguration(), daysThreshold);
		} catch (InvalidConnectionDataException e) {
			e.printStackTrace();
		}
		return null;
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

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
