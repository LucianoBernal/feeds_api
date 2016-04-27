package com.etermax.conversations.model;

import com.etermax.conversations.dto.*;

public class ConversationDataDisplayVisitor {
	public ConversationDataDTO visit(ConversationTextMessage conversationTextMessage) {
		return new TextMessageDisplayDTO(conversationTextMessage);
	}

	public ConversationDataDTO visit(ConversationAudioMessage conversationAudioMessage){
		return new AudioMessageDisplayDTO(conversationAudioMessage);
	}

	public ConversationDataDTO visit(ConversationVideoMessage conversationVideoMessage){
		return new VideoMessageDisplayDTO(conversationVideoMessage);
	}

	public ConversationDataDTO visit(ConversationImageMessage conversationImageMessage) {
		return new ImageMessageDisplayDTO(conversationImageMessage);
	}

	public ConversationDataDTO visit(Event event) {
		return new EventDTO(event);
	}

	public ConversationDataDTO visit(MessageReceipt messageReceipt) {
		return new MessageReceiptDTO(messageReceipt);
	}
}
