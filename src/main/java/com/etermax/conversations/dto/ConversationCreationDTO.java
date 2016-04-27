package com.etermax.conversations.dto;

import com.etermax.conversations.error.InvalidDTOException;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.error.InvalidUserIdException;
import com.etermax.conversations.error.TooFewUsersException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Set;

public class ConversationCreationDTO {
	@ApiModelProperty(required = true)
	@JsonProperty("users")
	private Set<Long> users;

	public Set<Long> getUsers() {
		return users;
	}

	public void setUsers(Set<Long> users) {
		this.users = users;
	}

	public void validate() throws InvalidDTOException {
		if (users == null || users.size() < 2) {
			throw new InvalidDTOException(new TooFewUsersException(), "Users must be at least two.");
		}
		for (Long user : users) {
			if (user < 1l) {
				throw new InvalidDTOException(new InvalidUserException(new InvalidUserIdException()), "Invalid user.");
			}
		}
	}
}
