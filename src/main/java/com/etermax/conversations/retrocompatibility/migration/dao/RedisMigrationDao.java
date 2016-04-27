package com.etermax.conversations.retrocompatibility.migration.dao;

import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.retrocompatibility.migration.command.RedisCommandFactory;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationApplication;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationApplications;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationMessage;
import com.etermax.jvon.core.JvonService;
import com.etermax.vedis.command.BoundedCommand;
import com.etermax.vedis.command.UnboundedCommand;
import com.etermax.vedis.command.hash.HGet;
import com.etermax.vedis.command.hash.HGetAll;
import com.etermax.vedis.command.hash.KeyOptimizer;
import com.etermax.vedis.command.set.SaddCommand;
import com.etermax.vedis.executor.SingleOperationExecutor;
import com.etermax.vedis.executor.bulk.BulkExecutor;
import com.etermax.vedis.executor.bulk.BulkResult;
import com.etermax.vedis.keys.VedisKey;
import com.etermax.vedis.keys.VedisKeyImpl;
import com.google.common.collect.Ordering;

import java.util.*;
import java.util.stream.Collectors;

public class RedisMigrationDao {

	private final static String READ_CHAT_HEADERS_KEY = "co:chatheads:rd:%d";
	private final static String UNREAD_CHAT_HEADERS_KEY = "co:chatheads:urd:%d";
	private final static String MESSAGES_KEY = "co:msgs:%d:%d";
	private final static String APPLICATIONS_KEY = "co:ula:";
	private final static String MIGRATIONS_KEY = "co:crkmig:%d";

	private SingleOperationExecutor singleExecutor;
	private BulkExecutor bulkExecutor;
	private Long daysThreshold;

	public RedisMigrationDao(SingleOperationExecutor singleExecutor, BulkExecutor bulkExecutor, Long daysThreshold) {
		this.singleExecutor = singleExecutor;
		this.bulkExecutor = bulkExecutor;
		this.daysThreshold = daysThreshold;
	}

	public Set<Long> getConversationInterlocutors(Long userId) {
		List<BoundedCommand<?>> commands = getChatHeadersCommands(userId);
		BulkResult result = bulkExecutor.execute(commands);

		return commands.stream()
					   .map(result::get)
					   .map(response -> (List<String>) response.get())
					   .flatMap(Collection::stream)
					   .map(Long::valueOf)
					   .collect(Collectors.toSet());
	}

	public Map<Long, Set<MigrationApplication>> getApplications(List<Long> userIds) {

		List<BoundedCommand<?>> commands = getApplicationCommands(userIds);

		BulkResult result = bulkExecutor.execute(commands);

		List<MigrationApplications> apps = commands.stream()
												   .map(result::get)
												   .map(response -> (String) response.get())
												   .map(this::deserializeApplications)
												   .collect(Collectors.toList());

		return generateApplicationsMap(userIds, apps, daysThreshold);
	}

	public List<MigrationMessage> getConversationMessages(Set<Long> userIds) {
		List<Long> sortedIds = Ordering.natural().sortedCopy(userIds);
		VedisKey key = new VedisKeyImpl(String.format(MESSAGES_KEY, sortedIds.get(0), sortedIds.get(1)));

		BoundedCommand<Map<String, String>> hGetAll = getMessagesCommand(sortedIds, key);
		Map<String, String> serializedMessages = singleExecutor.execute(hGetAll);

		return serializedMessages.values().stream().map(this::deserializeMessage).collect(Collectors.toList());
	}

	public Long setUserMigrated(Long userId) {
		Long shardingValue = userId % 16;
		VedisKeyImpl migrationsKey = new VedisKeyImpl(String.format(MIGRATIONS_KEY, shardingValue));
		return singleExecutor.execute(new SaddCommand(migrationsKey, String.valueOf(userId)).bound(shardingValue));
	}

	private List<BoundedCommand<?>> getChatHeadersCommands(Long userId) {
		VedisKey readKey = new VedisKeyImpl(String.format(READ_CHAT_HEADERS_KEY, userId));
		VedisKey unreadKey = new VedisKeyImpl(String.format(UNREAD_CHAT_HEADERS_KEY, userId));
		return Arrays.asList(getLRangeCommand(readKey).bound(userId), getLRangeCommand(unreadKey).bound(userId));
	}

	public List<BoundedCommand<?>> getApplicationCommands(List<Long> userIds) {
		return userIds.stream().map(this::getApplicationsCommand).collect(Collectors.toList());
	}

	private BoundedCommand<?> getApplicationsCommand(Long userId) {
		KeyOptimizer optimizer = new KeyOptimizer(APPLICATIONS_KEY, userId);
		return new HGet(optimizer.getImpl(), optimizer.getField()).bound(optimizer.getShardingValue());
	}

	private BoundedCommand<Map<String, String>> getMessagesCommand(List<Long> sortedIds, VedisKey key) {
		return new HGetAll(key).bound(sortedIds.get(0));
	}

	private UnboundedCommand<List<String>> getLRangeCommand(VedisKey key) {
		return RedisCommandFactory.createForRead(jedis -> jedis.lrange(key.getKeyIdentifier(), 0, -1),
												 pipeline -> pipeline.lrange(key.getKeyIdentifier(), 0, -1));
	}

	public Map<Conversation, List<MigrationMessage>> getConversationMessages(List<Conversation> conversations) {
		Map<Conversation, List<MigrationMessage>> result = new HashMap<>();

		Map<Conversation, BoundedCommand<?>> commandsByConversation = getConversationsCommands(conversations);
		List<BoundedCommand<?>> commands = new ArrayList<>(commandsByConversation.values());

		BulkResult response = bulkExecutor.execute(commands);

		conversations.stream().forEach(conversation -> {
			BoundedCommand<?> conversationCommand = commandsByConversation.get(conversation);
			Map<String, String> messagesMap = (Map<String, String>) response.get(conversationCommand).get();
			List<MigrationMessage> conversationMessages = deserializeMessages(messagesMap.values());
			result.put(conversation, conversationMessages);
		});

		return result;
	}

	public Map<Conversation, BoundedCommand<?>> getConversationsCommands(List<Conversation> conversations) {
		Map<Conversation, BoundedCommand<?>> result = new HashMap<>();
		conversations.stream().forEach(conversation -> {
			Set<Long> userIds = conversation.getUserIds();
			List<Long> sortedIds = Ordering.natural().sortedCopy(userIds);
			VedisKey key = new VedisKeyImpl(String.format(MESSAGES_KEY, sortedIds.get(0), sortedIds.get(1)));
			result.put(conversation, getMessagesCommand(sortedIds, key));
		});
		return result;
	}

	public List<MigrationMessage> deserializeMessages(Collection<String> messagesSerialized) {
		return messagesSerialized.stream()
								 .map(this::deserializeMessage)
								 .filter(m -> m.getText() != null & m.getDate() != null && m.getSender() != null)
								 .collect(Collectors.toList());
	}

	public MigrationApplications deserializeApplications(String serialized) {
		return JvonService.deserialize(serialized, MigrationApplications.class);
	}

	public MigrationMessage deserializeMessage(String serialized) {
		return JvonService.deserialize(serialized, MigrationMessage.class);
	}

	public Map<Long, Set<MigrationApplication>> generateApplicationsMap(List<Long> userIds,
			List<MigrationApplications> apps, Long daysThreshold) {
		Map<Long, Set<MigrationApplication>> resp = new HashMap<>();
		for (int i = 0; i < userIds.size(); i++) {
			MigrationApplications migrationApplications = Optional.ofNullable(apps.get(i))
																  .orElse(new MigrationApplications());
			resp.put(userIds.get(i), migrationApplications.getApplications(daysThreshold)
														  .stream()
														  .filter(migrationApplication -> !migrationApplication.equals(
																  MigrationApplication.BINGO_CRACK))
														  .collect(Collectors.toSet()));
		}
		return resp;
	}

	public Map<Long, Set<Long>> getConversationInterlocutors(Set<Long> userIds) {

		Map<Long, Set<Long>> response = new HashMap<>();
		Map<Long, List<BoundedCommand<?>>> commandsByUser = getChatHeaderCommands(userIds);
		List<BoundedCommand<?>> allCommands = flattenCommands(commandsByUser);
		BulkResult result = bulkExecutor.execute(allCommands);

		commandsByUser.entrySet().stream().forEach(entry -> {
			Set<Long> conversations = entry.getValue()
									 .stream()
									 .map(command -> result.get(command))
									 .map(r -> (List<String>) r.get())
									 .flatMap(Collection::stream)
									 .map(Long::valueOf)
									 .collect(Collectors.toSet());
			response.put(entry.getKey(), conversations);
		});

		return response;
	}

	private List<BoundedCommand<?>> flattenCommands(Map<Long, List<BoundedCommand<?>>> chatHeaderCommands) {
		return chatHeaderCommands.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

	private Map<Long, List<BoundedCommand<?>>> getChatHeaderCommands(Set<Long> userIds) {
		Map<Long, List<BoundedCommand<?>>> commands = new HashMap<>();
		userIds.stream().forEach(userId -> commands.put(userId, getChatHeadersCommands(userId)));
		return commands;
	}
}
