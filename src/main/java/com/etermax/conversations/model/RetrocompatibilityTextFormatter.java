package com.etermax.conversations.model;

public class RetrocompatibilityTextFormatter implements TextFormatter {
	@Override
	public String format(ConversationAudioMessage conversationAudioMessage) {
		return "audio";
	}

	@Override
	public String format(ConversationImageMessage conversationImageMessage) {
		return "image";
	}

	@Override
	public String format(ConversationTextMessage conversationTextMessage) {
		return conversationTextMessage.getText();
	}

	@Override
	public String format(ConversationVideoMessage conversationVideoMessage) {
		return "video";
	}
}
