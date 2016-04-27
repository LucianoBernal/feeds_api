package com.etermax.conversations.adapter;

import com.etermax.conversations.dto.ConversationCreationDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.dto.DeleteConversationDisplayDTO;
import com.etermax.conversations.error.ClientException;

import java.util.List;

public interface ConversationAdapter {
	ConversationDisplayDTO getConversation(String conversationId) throws ClientException;
	List<ConversationDisplayDTO> getUserConversations(Long userId) throws ClientException;
	ConversationDisplayDTO saveConversation(ConversationCreationDTO conversationCreationDTO) throws ClientException;
	DeleteConversationDisplayDTO deleteConversation(String conversationId, ConversationMessageDeletionDTO deletionDTO);
	List<ConversationDisplayDTO> getUserActiveConversations(Long userId, String application);
}
