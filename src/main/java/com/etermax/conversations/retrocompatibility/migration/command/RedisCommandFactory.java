package com.etermax.conversations.retrocompatibility.migration.command;

import com.etermax.vedis.connection.pool.ReadConnectionPool;
import com.etermax.vedis.connection.pool.WriteConnectionPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.function.Function;

public class RedisCommandFactory {


	public static <T> LambdaRedisCommand<T> createForRead(Function<Jedis, T> nonPipelineFunction,
			Function<Pipeline, Response<T>> pipelineFunction) {
		return new LambdaRedisCommand<>(nonPipelineFunction, pipelineFunction, ReadConnectionPool::new);
	}

	public static <T> LambdaRedisCommand<T> createForWrite(Function<Jedis, T> nonPipelineFunction,
			Function<Pipeline, Response<T>> pipelineFunction) {
		return new LambdaRedisCommand<>(nonPipelineFunction, pipelineFunction, WriteConnectionPool::new);
	}

}
