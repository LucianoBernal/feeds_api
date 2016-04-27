package com.etermax.conversations.adapter;

import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.MessageVisitor;

import java.util.List;
import java.util.Map;

public interface MessageAdapter extends MessageVisitor {

	void deleteMessage(String conversationId, String messageId,
			ConversationMessageDeletionDTO conversationMessageDeletionDTO) throws ClientException;

	List<AddressedMessageDisplayDTO> getRetrocompatibilityUserMessages(List<Long> userIds, String dateString,
			String application);

	AddressedMessageDisplayDTO saveMessage(AddressedMessageCreationDTO messageDisplayDTO) throws ClientException;

	Map<String, AddressedMessageDisplayDTO> getLastMessages(Long userId, List<ConversationDisplayDTO> userConversations,
			String application);
}
