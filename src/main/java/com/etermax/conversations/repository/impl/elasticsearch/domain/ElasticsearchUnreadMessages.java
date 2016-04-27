package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.List;
import java.util.stream.Collectors;

public class ElasticsearchUnreadMessages {
	private List<UnreadMessageData> unreadMessages;

	public Long getUnread(String key) {
		List<UnreadMessageData> unread = unreadMessages.stream().filter(unreadData ->
				unreadData.getKey().equals(key)).collect(Collectors.toList());
		return unread.isEmpty() ? 0L : unread.get(0).getValue();
	}
}
