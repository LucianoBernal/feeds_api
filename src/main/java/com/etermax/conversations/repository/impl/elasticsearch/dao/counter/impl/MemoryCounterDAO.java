package com.etermax.conversations.repository.impl.elasticsearch.dao.counter.impl;

import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.CounterDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryCounterDAO implements CounterDAO {

	public Map<String, AtomicLong> counters;

	public MemoryCounterDAO() {
		this.counters = new ConcurrentHashMap<>();
	}

	@Override
	public Long incrementUnreadMessages(Long userId, String conversationId, String application) {
		AtomicLong counter = counters.get(getKey(userId, conversationId, application));
		if (counter == null) {
			counters.put(getKey(userId, conversationId, application), new AtomicLong(1));
			return 1L;
		}
		return counter.addAndGet(1);
	}

	@Override
	public Long getUnreadMessages(Long userId, String conversationId, String application) {
		AtomicLong counter = counters.get(getKey(userId, conversationId, application));
		if (counter == null) {
			return 0L;
		} else {
			return counter.get();
		}
	}

	@Override
	public Map<String, Long> getUnreadMessages(Long userId, List<String> conversationIds, String application) {
		Map<String, Long> results = new HashMap<>();
		conversationIds.forEach(s -> results.put(s, getUnreadMessages(userId, s, application)));
		return results;
	}

	@Override
	public void resetUnreadMessages(Long userId, String conversationId, String application) {
		AtomicLong counter = counters.get(getKey(userId, conversationId, application));
		if (counter != null) {
			counter.set(0);
		}
	}


	private String getKey(Long userId, String conversationId, String application) {
		return userId + "-" + conversationId + "-" + application;
	}

}
