package com.etermax.conversations.repository.impl.elasticsearch.dao.counter.impl;

import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.CounterDAO;
import com.etermax.vedis.command.BoundedCommand;
import com.etermax.vedis.executor.SingleOperationExecutor;
import com.etermax.vedis.executor.bulk.BulkExecutor;
import com.etermax.vedis.executor.bulk.BulkResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RedisCounterDAO implements CounterDAO {

	private final static String UNREAD_MESSAGES_KEY = "crk:um:%s";
	private final static String UNREAD_MESSAGES_FIELD = "%d-%s";
	private SingleOperationExecutor singleExecutor;
	private BulkExecutor bulkExecutor;

	public RedisCounterDAO(SingleOperationExecutor singleExecutor, BulkExecutor bulkExecutor) {
		this.singleExecutor = singleExecutor;
		this.bulkExecutor = bulkExecutor;
	}

	@Override
	public Long incrementUnreadMessages(Long userId, String conversationId, String application) {
		try{
			return singleExecutor.execute(
					getIncrementCounterCommand(userId, conversationId, application).bound(getHashedId(conversationId)));
		}catch (Exception e){
			return 0L;
		}
	}

	@Override
	public Long getUnreadMessages(Long userId, String conversationId, String application) {
		try{
			String counterString = singleExecutor.execute(
					getGetCounterCommand(userId, conversationId, application).bound(getHashedId(conversationId)));
			return counterString == null ? 0 : Long.valueOf(counterString);
		}catch (Exception e){
			return 0L;
		}
	}

	@Override
	public Map<String, Long> getUnreadMessages(Long userId, List<String> conversationIds, String application) {
		Map<String, Long> response = new HashMap<>();
		try{
			List<BoundedCommand<?>> commands = getCounterCommands(userId, conversationIds, application);
			BulkResult result = bulkExecutor.execute(commands);
			List<Long> results = commands.stream()
										 .map(command -> result.get(command))
										 .map(r -> Optional.ofNullable(r.get()))
										 .map(r -> r.map(o -> Long.valueOf((String) o)).orElse(0L))
										 .collect(Collectors.toList());

			for (int i = 0; i < results.size(); i++) {
				try{
					Long value = results.get(i);
					if(value == null){
						value = 0L;
					}
					response.put(conversationIds.get(i), value);
				}catch (Exception e){
					response.put(conversationIds.get(i), 0L);
				}
			}
		}catch (Exception e){
			conversationIds.forEach(s -> response.put(s, 0L));
		}

		return response;
	}

	@Override
	public void resetUnreadMessages(Long userId, String conversationId, String application) {
		try{
			singleExecutor.execute(
					getDeleteCounterCommand(userId, conversationId, application).bound(getHashedId(conversationId)));
		}catch (Exception ignore){}
	}

	private LambdaRedisCommand<Long> getIncrementCounterCommand(Long userId, String conversationId,
			String application) {
		String key = getUnreadMessagesKey(conversationId);
		String field = getUnreadMessagesField(userId, application);
		return RedisCommandFactory.createForWrite(jedis -> jedis.hincrBy(key, field, 1),
												  pipeline -> pipeline.hincrBy(key, field, 1));
	}

	private LambdaRedisCommand<String> getGetCounterCommand(Long userId, String conversationId, String application) {
		String key = getUnreadMessagesKey(conversationId);
		String field = getUnreadMessagesField(userId, application);
		return RedisCommandFactory.createForRead(jedis -> jedis.hget(key, field), pipeline -> pipeline.hget(key, 
																											field));
	}

	private LambdaRedisCommand<Long> getDeleteCounterCommand(Long userId, String conversationId, String application) {
		String key = getUnreadMessagesKey(conversationId);
		String field = getUnreadMessagesField(userId, application);
		return RedisCommandFactory.createForWrite(jedis -> jedis.hdel(key, field),
												  pipeline -> pipeline.hdel(key, field));
	}

	private List<BoundedCommand<?>> getCounterCommands(Long userId, List<String> conversationIds, String application) {
		return conversationIds.stream()
							  .map(id -> getGetCounterCommand(userId, id, application).bound(getHashedId(id)))
							  .collect(Collectors.toList());
	}

	private String getUnreadMessagesField(Long userId, String application) {
		return String.format(UNREAD_MESSAGES_FIELD, userId, application);
	}

	private String getUnreadMessagesKey(String conversationId) {
		return String.format(UNREAD_MESSAGES_KEY, conversationId);
	}

	private Long getHashedId(String conversationId) {
		return Long.valueOf(conversationId.split("-")[0]);
	}

}
