package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.dto.TextMessageCreationDTO;

public interface MessageVisitor {

    ConversationDataDTO saveMessage(TextMessageCreationDTO textMessageDTO, String conversationId);

}
