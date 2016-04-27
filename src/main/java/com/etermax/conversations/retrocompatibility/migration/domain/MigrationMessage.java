package com.etermax.conversations.retrocompatibility.migration.domain;

import com.etermax.jvon.annotations.JvonProperty;

public class MigrationMessage {

	@JvonProperty("b")
	private String text;
	@JvonProperty("c")
	private Long date;
	@JvonProperty("a")
	private Long sender;

	public MigrationMessage() {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Long getSender() {
		return sender;
	}

	public void setSender(Long sender) {
		this.sender = sender;
	}
}
