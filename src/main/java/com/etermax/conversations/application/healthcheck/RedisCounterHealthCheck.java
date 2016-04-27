package com.etermax.conversations.application.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import com.etermax.vedis.connection.VedisConnectionConfiguration;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;
import com.etermax.vedis.executor.AllRedisExecutionResult;
import com.etermax.vedis.keys.Ping;
import jersey.repackaged.com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class RedisCounterHealthCheck extends HealthCheck {
	RedisHealthCheckConfiguration redisHealthCheckConfiguration;

	public RedisCounterHealthCheck(VedisConnectionConfiguration connectionConfiguration)
			throws InvalidConnectionDataException {
			redisHealthCheckConfiguration = new RedisHealthCheckConfiguration(
					connectionConfiguration);
	}

	@Override
	protected Result check() throws Exception {
		List<String> unreachableServers = Lists.newArrayList();
		evaluateShardedServers(unreachableServers);
		evaluateStaticServers(unreachableServers);
		return evaluateErrors(unreachableServers);
	}

	private Result evaluateErrors(List<String> unreachableServers) {
		if (hasErrors(unreachableServers)) {
			return Result.unhealthy(buildErrorMessage(unreachableServers));
		}
		return Result.healthy();
	}

	private void evaluateStaticServers(List<String> unreachableServers) {
		String serverName = "static";
		try {
			String responseOnStaticServer = redisHealthCheckConfiguration
					.executeOnStaticServer(new Ping());
			evaluateResponse(unreachableServers, responseOnStaticServer, serverName);
		} catch (Exception e) {
			unreachableServers.add(serverName);
		}
	}

	private void evaluateShardedServers(List<String> unreachableServers) {
		AllRedisExecutionResult<String> pingOnShardedServers = redisHealthCheckConfiguration
				.executeOnEveryShardedServer(
						new Ping());
		evaluateExceptions(unreachableServers, pingOnShardedServers);
		evaluateResponses(unreachableServers, pingOnShardedServers);
	}

	private void evaluateResponses(List<String> unreachableServers,
			AllRedisExecutionResult<String> pingOnShardedServers) {
		for (Map.Entry<Integer, String> entry : pingOnShardedServers.getResults().entrySet()) {
			String response = entry.getValue();
			Integer serverId = entry.getKey();
			evaluateResponse(unreachableServers, response, serverId.toString());
		}
	}

	private void evaluateExceptions(List<String> unreachableServers,
			AllRedisExecutionResult<String> pingOnShardedServers) {
		for (Map.Entry<Integer, Exception> errors : pingOnShardedServers.getErrors().entrySet()) {
			unreachableServers.add(errors.getKey().toString());
		}
	}

	private void evaluateResponse(List<String> unreachableServers, String response, String serverId) {
		if (!"PONG".equalsIgnoreCase(response)) {
			unreachableServers.add(serverId);
		}
	}

	private String buildErrorMessage(List<String> unreachableServers) {
		StringBuilder builder = new StringBuilder();
		builder.append("Los siguientes nodos de redis no responden: ");
		for (String server : unreachableServers) {
			builder.append(server).append(",");
		}
		String message = builder.toString();
		return message.substring(0, message.length() - 1);
	}

	private boolean hasErrors(List<String> unreachableServers) {
		return !unreachableServers.isEmpty();
	}
}
