package com.etermax.conversations.adapter.impl;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationCreationDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.dto.DeleteConversationDisplayDTO;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.error.*;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.service.ConversationService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ConversationAdapterImpl implements ConversationAdapter {
	private ConversationService conversationService;

	public ConversationAdapterImpl(ConversationService conversationService) {
		this.conversationService = conversationService;
	}

	@Override
	public ConversationDisplayDTO getConversation(String conversationId) throws ClientException {
		Conversation conversation;
		try {
			conversation = conversationService.getConversation(conversationId);
		} catch (GetConversationException e) {
			throw new ClientException(e, 400);
		}
		return new ConversationDisplayDTO(conversation);
	}

	@Override
	public List<ConversationDisplayDTO> getUserConversations(Long userId) {
		List<Conversation> conversations = conversationService.getUserConversations(userId);
		return conversations.stream().map(ConversationDisplayDTO::new).collect(Collectors.toList());
	}

	public List<ConversationDisplayDTO> getUserActiveConversations(Long userId, String application) {
		List<Conversation> conversations = conversationService.getUserActiveConversations(userId, application);
		return conversations.stream().map(ConversationDisplayDTO::new).collect(Collectors.toList());
	}

	@Override
	public ConversationDisplayDTO saveConversation(ConversationCreationDTO conversationCreationDTO) {
		try {
			conversationCreationDTO.validate();
		} catch (InvalidDTOException e) {
			throw new ClientException(e, 400);
		}

		Conversation savedConversation = conversationService.saveConversation(conversationCreationDTO.getUsers());
		return new ConversationDisplayDTO(savedConversation);
	}

	@Override
	public DeleteConversationDisplayDTO deleteConversation(String conversationId, ConversationMessageDeletionDTO deletionDTO) {
		try {
			deletionDTO.validate();
			Long userId = deletionDTO.getUserId();
			String application = deletionDTO.getApplication();
			Date deletionDate = new Date();
			conversationService.deleteConversation(conversationId, userId, application, deletionDate);
			return new DeleteConversationDisplayDTO(deletionDate);
		} catch (InvalidDTOException | OperationException e) {
			throw new ClientException(e, 400);
		} catch (ModelException e) {
			throw new ServerException(e, "");
		}
	}
}
