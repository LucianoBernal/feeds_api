package com.etermax.conversations.repository.impl.elasticsearch.domain;

public class DeletionData {
	private String key;
	private Long value;

	public DeletionData(String key, Long value) {
		this.key = key;
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
