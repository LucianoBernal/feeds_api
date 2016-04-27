package com.etermax.conversations.notification.sender.factory.impl;

import com.etermax.bagdes.service.BadgeService;
import com.etermax.conversations.notification.model.NotificationSender;
import com.etermax.conversations.notification.sender.factory.NotificationSenderFactory;
import com.etermax.conversations.notification.sender.hermes.HermesNotificationSender;
import com.etermax.hermes.common.notification.config.HermesModuleConfiguration;
import com.etermax.hermes.common.notification.dispatch.*;
import com.etermax.hermes.devices.factory.DeviceServiceFactory;
import com.etermax.hermes.devices.model.*;
import com.etermax.hermes.devices.service.DeviceService;
import com.etermax.vedis.connection.VedisConnectionConfiguration;
import com.etermax.vedis.connection.VedisConnectionManager;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import jersey.repackaged.com.google.common.collect.Lists;
import retrocompatibility.client.UsersRetrocompatibleClient;
import retrocompatibility.client.UsersRetrocompatibleClientBuilder;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HermesNotificationSenderFactory implements NotificationSenderFactory {

	@NotNull
	@JsonProperty("hermes_inbox_url")
	private String hermesInboxUrl;

	@JsonProperty("hermes_max_auto_retries")
	private String maxAutoRetries = "0";

	@JsonProperty("hermes_max_auto_retries_next_server")
	private String maxAutoRetriesNextServer = "0";

	@JsonProperty("hermes_connect_timeout")
	private String connectTimeout = "2000";

	@JsonProperty("hermes_read_timeout")
	private String readTimeout = "2000";

	@NotNull
	@JsonProperty("redis_servers")
	private String servers;

	@NotNull
	@JsonProperty("static_server")
	private String staticServer;

	@JsonProperty
	private String password;

	@JsonProperty("time_between_eviction_runs_millis")
	private Integer timeBetweenEvictionRunsMillis = 3000;

	@JsonProperty("min_evictable_idle_time_millis")
	private Long minEvictableIdleTimeMillis = 600000L;

	@JsonProperty("min_idle")
	private Integer minIdle = 2;

	@JsonProperty("max_idle")
	private Integer maxIdle = -1;

	@JsonProperty("max_active")
	private Integer maxActive = 70;

	@JsonProperty("max_wait")
	private Integer maxWait = 10000;

	@JsonProperty("test_on_borrow")
	private Boolean testOnBorrow = false;

	@JsonProperty("test_while_idle")
	private Boolean testWhileIdle = true;

	@JsonProperty
	private Integer timeout = 0;

	@JsonProperty("when_exhausted_action")
	private Integer whenExhaustedAction = 1;

	@JsonProperty("daysThreshold")
	private Long daysThreshold = 31l;

	@NotNull
	@JsonProperty("users_api_url")
	private String usersApiUrl;

	@Override
	public NotificationSender createSender() {
		VedisConnectionManager connectionManager = createVedisConnectionManager();
		BadgeService badgeService = createBadgeService(connectionManager);
		UsersRetrocompatibleClient usersRetrocompatibleClient = createUsersRetrocompatibleClient();
		NotificationDispatcher notificationDispatcher = createNotificationDispatcher(connectionManager);
		return new HermesNotificationSender(notificationDispatcher, usersRetrocompatibleClient, badgeService);
	}

	private UsersRetrocompatibleClient createUsersRetrocompatibleClient() {
		UsersRetrocompatibleClientBuilder usersRetrocompatibleClientBuilder = new UsersRetrocompatibleClientBuilder();
		return usersRetrocompatibleClientBuilder.build(usersApiUrl);
	}

	private BadgeService createBadgeService(VedisConnectionManager connectionManager) {
		return new BadgeService(connectionManager);
	}

	private NotificationDispatcher createNotificationDispatcher(VedisConnectionManager connectionManager) {
		HermesModuleConfiguration hermesModuleConfig = new HermesModuleConfiguration(hermesInboxUrl, maxAutoRetries,
				maxAutoRetriesNextServer, connectTimeout, readTimeout);
		DefaultHermesDispatcher inboxDispatcher = new DefaultHermesDispatcher(hermesModuleConfig);

		DeviceService deviceService = createDeviceService(connectionManager);
		NotificationPetitionFactory factory = new DefaultNotificationPetitionFactory(deviceService);
		return new HttpNotificationDispatcher(factory, inboxDispatcher);
	}

	private DeviceService createDeviceService(VedisConnectionManager connectionManager) {
		return DeviceServiceFactory.build(connectionManager);
	}

	private VedisConnectionManager createVedisConnectionManager() {
		VedisConnectionManager connectionManager = null;
		try {
			connectionManager = new VedisConnectionManager(createVedisConfiguration());
		} catch (InvalidConnectionDataException e) {
			e.printStackTrace();
		}
		return connectionManager;
	}

	private VedisConnectionConfiguration createVedisConfiguration() {
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
