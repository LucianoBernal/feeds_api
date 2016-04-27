package com.etermax.conversations.retrocompatibility.adapter;

import com.etermax.conversations.error.*;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityConversationMessageDeletionDTO;
import com.etermax.conversations.service.ConversationService;
import com.etermax.conversations.service.MessageService;
import com.google.common.collect.Sets;

public class RetrocompatibilityMessageAdapter {

	private ConversationService conversationService;
	private MessageService messageService;

	public RetrocompatibilityMessageAdapter(ConversationService conversationService, MessageService messageService) {
		this.conversationService = conversationService;
		this.messageService = messageService;
	}

	public void deleteMessage(RetrocompatibilityConversationMessageDeletionDTO deletionDTO) {
		try {
			deletionDTO.validate();
			Conversation conversationWithUsers = conversationService
					.getConversationWithUsers(Sets.newHashSet(deletionDTO.getFirstUserId(), deletionDTO.getSecondUserId()));
			String conversationId = conversationWithUsers.getId();
			String application = messageService.getMessageApplication(conversationId, deletionDTO.getMessageId());
			messageService.deleteMessage(conversationId, deletionDTO.getMessageId(), deletionDTO.getFirstUserId(), application);
		} catch (MessageNotFoundException | ConversationNotFoundException | DeleteMessageException| InvalidDTOException e) {
			throw new ClientException(e, 400);
		}
	}
}
