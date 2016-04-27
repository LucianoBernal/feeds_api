package com.etermax.conversations.retrocompatibility.service;

import client.UsersClient;
import client.UsersClientBuilder;
import dto.UserDTO;
import retrocompatibility.client.UsersRetrocompatibleClient;
import retrocompatibility.client.UsersRetrocompatibleClientBuilder;
import retrocompatibility.dto.RetrocompatibilityUserDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RetrocompatibilityUserService {

	private UsersRetrocompatibleClient usersRetrocompatibilityClient;
	private UsersClient usersClient;

	public RetrocompatibilityUserService(String url) {
		this.usersRetrocompatibilityClient = new UsersRetrocompatibleClientBuilder().build(url);
		this.usersClient = new UsersClientBuilder().build(url);
	}

	public List<RetrocompatibilityUserDTO> getUsers(List<Long> receiverList, Long userId) {
		if (receiverList == null || receiverList.isEmpty()) {
			return new ArrayList<>();
		}
		return usersRetrocompatibilityClient.getUsers(receiverList, userId).toBlocking().last();
	}

	public UserDTO getUser(Long viewAs, Long user) {
		return usersClient.getUsers(Collections.singletonList(user), viewAs).toBlocking().last().get(0);
	}
}
