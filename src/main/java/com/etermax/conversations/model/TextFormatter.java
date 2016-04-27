package com.etermax.conversations.model;

public interface TextFormatter {
	String format(ConversationAudioMessage conversationAudioMessage);

	String format(ConversationImageMessage conversationImageMessage);

	String format(ConversationTextMessage conversationTextMessage);

	String format(ConversationVideoMessage conversationVideoMessage);
}
