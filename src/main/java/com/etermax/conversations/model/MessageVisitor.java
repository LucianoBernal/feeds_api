package com.etermax.conversations.model;

import com.etermax.conversations.dto.*;

public interface MessageVisitor {

    ConversationDataDTO saveMessage(TextMessageCreationDTO textMessageDTO, String conversationId);

    ConversationDataDTO saveMessage(AudioMessageCreationDTO audioMessageDTO, String conversationId);

    ConversationDataDTO saveMessage(ImageMessageCreationDTO imageMessageDTO, String conversationId);

    ConversationDataDTO saveMessage(VideoMessageCreationDTO videoMessageDTO, String conversationId);


}
