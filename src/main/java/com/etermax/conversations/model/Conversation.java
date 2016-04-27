package com.etermax.conversations.model;

import com.etermax.conversations.error.InvalidConversation;
import com.etermax.conversations.error.TooFewUsersException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Conversation {

	private Set<User> users;
	private String id;
	private Date lastUpdated;

	public Conversation(Set<User> users, Date lastUpdated) throws InvalidConversation {
		validateUsers(users);
		this.lastUpdated = lastUpdated;
		this.users = users;
	}

	public Conversation(Set<User> users) throws InvalidConversation {
		validateUsers(users);
		this.lastUpdated = new Date();
		this.users = users;
	}

	private void validateUsers(Set<User> users) throws InvalidConversation {
		if (users.size() < 2) {
			throw new InvalidConversation(new TooFewUsersException());
		}
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Set<User> getUsers() {
		return users;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Long> getUserIds() {
		return new HashSet<>(users.stream().map(User::getId).collect(Collectors.toList()));
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
