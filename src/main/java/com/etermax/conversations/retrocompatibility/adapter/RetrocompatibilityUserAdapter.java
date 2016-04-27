package com.etermax.conversations.retrocompatibility.adapter;

import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityUserService;
import dto.UserDTO;
import retrocompatibility.dto.RetrocompatibilityUserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class RetrocompatibilityUserAdapter {
	private RetrocompatibilityUserService retrocompatibilityUserService;

	public RetrocompatibilityUserAdapter(RetrocompatibilityUserService retrocompatibilityUserService) {
		this.retrocompatibilityUserService = retrocompatibilityUserService;
	}

	public List<RetrocompatibilityUserDTO> getReceivers(Long userId, List<ConversationDisplayDTO> userConversations) {
		List<Long> receiverList = userConversations.stream()
				.map(conversationDisplayDTO -> getReceiver(userId, conversationDisplayDTO))
				.collect(Collectors.toList());
		return retrocompatibilityUserService.getUsers(receiverList, userId);
	}

	private Long getReceiver(Long userId, ConversationDisplayDTO conversationDisplayDTO) {
		return conversationDisplayDTO.getUsers().stream().filter(user -> !user.equals(userId))
				.collect(Collectors.toList()).get(0);
	}

	public UserDTO getUser(List<Long> userIds) {
		return retrocompatibilityUserService.getUser(userIds.get(0), userIds.get(1));
	}
}
