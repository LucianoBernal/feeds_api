package com.etermax.conversations.model;

import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.error.InvalidUserIdException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class User {
	private Long id;

	public User(Long userId) throws InvalidUserException {
		if (userId.compareTo(0l) <= 0) {
			throw new InvalidUserException(new InvalidUserIdException());
		}
		id = userId;
	}

	public Long getId() {
		return id;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		User user = (User) obj;
		return new EqualsBuilder().append(id, user.getId()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				append(id).
				toHashCode();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
