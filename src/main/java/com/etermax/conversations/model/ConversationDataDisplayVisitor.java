package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.dto.EventDTO;
import com.etermax.conversations.dto.MessageReceiptDTO;
import com.etermax.conversations.dto.TextMessageDisplayDTO;

public class ConversationDataDisplayVisitor {
	public ConversationDataDTO visit(ConversationTextMessage conversationTextMessage) {
		return new TextMessageDisplayDTO(conversationTextMessage);
	}

	public ConversationDataDTO visit(Event event) {
		return new EventDTO(event);
	}

	public ConversationDataDTO visit(MessageReceipt messageReceipt) {
		return new MessageReceiptDTO(messageReceipt);
	}
}
