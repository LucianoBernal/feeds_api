package com.etermax.conversations.dto;

import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.stream.Collectors;

public class ConversationDisplayDTO {

	@JsonProperty("id") private String id;

	@JsonProperty("users") private Set<Long> users;

	public ConversationDisplayDTO(Conversation conversation) {
		Set<Long> userIds = conversation.getUsers().stream().map(User::getId).collect(Collectors.toSet());
		setUsers(userIds);
		setId(conversation.getId());
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public Set<Long> getUsers() {
		return users;
	}

	private void setUsers(Set<Long> users) {
		this.users = users;
	}

}
