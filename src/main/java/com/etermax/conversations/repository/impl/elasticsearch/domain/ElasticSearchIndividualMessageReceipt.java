package com.etermax.conversations.repository.impl.elasticsearch.domain;

public class ElasticSearchIndividualMessageReceipt {
	private String type;
	private Long user;
	private Long date;

	public ElasticSearchIndividualMessageReceipt(String type, Long userId,
			Long date) {

		this.type = type;
		this.user = userId;
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public Long getUserId() {
		return user;
	}

	public Long getDate() {
		return date;
	}
}
