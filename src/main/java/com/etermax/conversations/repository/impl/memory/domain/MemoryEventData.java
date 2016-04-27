package com.etermax.conversations.repository.impl.memory.domain;

public class MemoryEventData {

	private String key;
	private String value;

	public MemoryEventData(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
