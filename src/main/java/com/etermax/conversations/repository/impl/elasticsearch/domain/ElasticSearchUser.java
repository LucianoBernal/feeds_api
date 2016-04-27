package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.List;

public class ElasticSearchUser {
	private Long id;
	private List<String> conversations;

	public ElasticSearchUser(Long id, List<String> conversations) {
		this.id = id;
		this.conversations = conversations;
	}

	public Long getId() {
		return id;
	}

	public List<String> getConversations() {
		return conversations;
	}
}
