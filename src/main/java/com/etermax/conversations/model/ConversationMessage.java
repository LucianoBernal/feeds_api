package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ConversationMessageMapperVisitor;
import com.etermax.conversations.repository.impl.elasticsearch.domain.ElasticSearchMessage;

import java.util.Date;

public interface ConversationMessage extends ConversationData {

	Date getDate();

	User getSender();

	String getId();

	Boolean getIgnored();

	void setId(String messageId);

	String getConversationId();

	ConversationDataDTO accept(ConversationDataDisplayVisitor conversationDataDisplayVisitor);

	String getApplication();

	void addMessageReceipt(MessageReceipt messageReceipt);

	MessageReceipt getMessageReceipt();

	ElasticSearchMessage accept(ConversationMessageMapperVisitor conversationMessageMapperVisitor);

	String acceptFormatter(TextFormatter textFormatter);

}
