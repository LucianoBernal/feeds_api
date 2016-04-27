package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;

import java.util.Date;

public interface ConversationData {
	String getConversationId();

	Date getDate();

	String getApplication();

	String getType();

	String getId();

	ConversationDataDTO accept(ConversationDataDisplayVisitor conversationDataDisplayVisitor);

}
