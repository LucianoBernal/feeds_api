package com.etermax.conversations.model;

import java.util.Date;

public class AddressedMessage {

	private String text;
	private User sender;
	private User user;
	private Date date;
	private String id;
	private String application;
	private Boolean blocked;

	public AddressedMessage(String text, User sender, User user, Date date, String application, Boolean blocked) {
		this.text = text;
		this.sender = sender;
		this.date = date;
		this.user = user;

		this.application = application;
		this.blocked = blocked;
	}

	public String getText() {
		return text;
	}

	public User getSender() {
		return sender;
	}

	public Boolean getBlocked() {
		return blocked;
	}

	public Date getDate() {
		return date;
	}

	public User getUser() {
		return user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplication() {
		return application;
	}
}
