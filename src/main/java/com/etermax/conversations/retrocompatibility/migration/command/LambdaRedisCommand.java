package com.etermax.conversations.retrocompatibility.migration.command;

import com.etermax.vedis.command.UnboundedCommand;
import com.etermax.vedis.connection.VedisConnectionManager;
import com.etermax.vedis.connection.pool.ReadConnectionPool;
import com.etermax.vedis.connection.pool.VedisConnectionPool;
import com.etermax.vedis.connection.pool.WriteConnectionPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.function.Function;

public class LambdaRedisCommand<T> extends UnboundedCommand<T> {

	private Function<Jedis, T> nonPipelineFunction;
	private Function<Pipeline, Response<T>> pipelineFunction;
	private Function<VedisConnectionManager, VedisConnectionPool> poolFunction;

	public LambdaRedisCommand(Function<Jedis, T> nonPipelineFunction, Function<Pipeline, Response<T>> pipelineFunction,
			Function<VedisConnectionManager, VedisConnectionPool> poolFunction) {
		this.nonPipelineFunction = nonPipelineFunction;
		this.pipelineFunction = pipelineFunction;
		this.poolFunction = poolFunction;
	}

	@Override
	public T execute(Jedis jedis) {
		return nonPipelineFunction.apply(jedis);
	}

	@Override
	public Response<T> execute(Pipeline pipeline) {
		return pipelineFunction.apply(pipeline);
	}

	@Override
	public VedisConnectionPool buildPool(VedisConnectionManager vedisConnectionManager) {
		return poolFunction.apply(vedisConnectionManager);
	}
}
