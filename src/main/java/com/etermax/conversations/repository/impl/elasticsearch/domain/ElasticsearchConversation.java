package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ElasticsearchConversation {

	private String id;
	private Set<Long> users;
	private String type;
	private List<UnreadMessageData> unreadMessages;
	private List<DeletionData> deletedBy;
	private Long lastActivity;

	public ElasticsearchConversation(String id, Set<Long> users, String type) {
		this.id = id;
		this.users = users;
		this.type = type;
		this.unreadMessages = new ArrayList<>();
		this.deletedBy = new ArrayList<>();
		this.lastActivity = new Date().getTime();
	}

	public Long getLastActivity() {
		return lastActivity != null? lastActivity : 0L;
	}

	public String getId() {
		return id;
	}

	public Boolean hasDeleted(String key) {
		return deletedBy.stream().anyMatch(deletionData -> deletionData.getKey().equals(key));
	}

	public String getType() {
		return type;
	}

	public Long getUnreadMessages(String key) {
		List<UnreadMessageData> un = unreadMessages.stream()
												   .filter(unreadMessageData -> unreadMessageData.getKey().equals(key))
												   .collect(Collectors.toList());
		return un.isEmpty() ? 0L : un.get(0).getValue();
	}

	public Set<Long> getUsers() {
		return users;
	}

	public Long getDeletedBy(String key) {
		List<DeletionData> del = deletedBy.stream()
										  .filter(deletionData -> deletionData.getKey().equals(key))
										  .collect(Collectors.toList());
		return del.isEmpty() ? 0L : del.get(0).getValue();
	}

	public Long getDeletedBy(Long userId, String app) {
		List<DeletionData> del = deletedBy.stream()
										  .filter(deletionData -> deletionData.getKey().equals(userId + "-" + app))
										  .collect(Collectors.toList());
		return del.isEmpty() ? 0L : del.get(0).getValue();
	}

	public void setDeletedBy(List<DeletionData> deletedBy) {
		this.deletedBy = deletedBy;
	}
}
