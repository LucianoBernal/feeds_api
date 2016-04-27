package com.etermax.conversations.repository.impl.elasticsearch.dao.counter;

import java.util.List;
import java.util.Map;

public interface CounterDAO {
	Long incrementUnreadMessages(Long userId, String conversationId, String application);
	Long getUnreadMessages(Long userId, String conversationId, String application);
	Map<String, Long> getUnreadMessages(Long userId, List<String> conversationIds, String application);
	void resetUnreadMessages(Long userId, String conversationId, String application);
}
