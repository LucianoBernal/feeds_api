package com.etermax.conversations.model;

public class RetrocompatibilityMessageVisitor {
	public String visit(ConversationAudioMessage conversationAudioMessage) {
		return "audio";
	}

	public String visit(ConversationImageMessage conversationImageMessage) {
		return "image";
	}

	public String visit(ConversationTextMessage conversationTextMessage) {
		return conversationTextMessage.getText();
	}

	public String visit(ConversationVideoMessage conversationVideoMessage) {
		return "video";
	}
}
