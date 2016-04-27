package com.etermax.conversations.application.healthcheck;

import com.etermax.vedis.command.UnboundedCommand;
import com.etermax.vedis.command.VedisCommand;
import com.etermax.vedis.connection.VedisConnectionConfiguration;
import com.etermax.vedis.connection.VedisConnectionManager;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;
import com.etermax.vedis.executor.AllRedisExecutionResult;
import com.etermax.vedis.executor.AllShardedServerExecutor;
import com.etermax.vedis.executor.SingleOperationExecutor;

public class RedisHealthCheckConfiguration {
	private static RedisHealthCheckConfiguration INSTANCE;
	private VedisConnectionManager connectionManager;
	private SingleOperationExecutor singleOperatorExecutor;
	private AllShardedServerExecutor allShardedRedisExecutor;

	public <T> T executeOnStaticServer(UnboundedCommand<T> command) {
		return singleOperatorExecutor.execute(command.bound(new Long(VedisConnectionManager.STATIC_SERVER_NUMBER)));
	}

	public <T> AllRedisExecutionResult<T> executeOnEveryShardedServer(VedisCommand<T> command) {
		return allShardedRedisExecutor.execute(command);
	}

	public RedisHealthCheckConfiguration(VedisConnectionConfiguration configuration) throws
			InvalidConnectionDataException {
		connectionManager = new VedisConnectionManager(configuration);
		singleOperatorExecutor = new SingleOperationExecutor(connectionManager);
		allShardedRedisExecutor = new AllShardedServerExecutor(connectionManager);
	}

}
