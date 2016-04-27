package com.etermax.conversations.factory;

import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.model.*;

import java.util.Date;

public class ConversationMessageFactory {

	public ConversationMessage createTextConversationMessage(String text, User sender, String conversationId,
			String application, Boolean ignored) throws InvalidMessageException {
		Date date = new Date();
		return new ConversationTextMessage(sender, conversationId, date, text, application, ignored);

	}

	public ConversationMessage createAudioConversationMessage(String url, Long length, User sender, String conversationId,
			String format, String application, Boolean ignored) throws InvalidMessageException {
		return new ConversationAudioMessage(sender, conversationId, new Date(), url, length, format, application, ignored);
	}

	public ConversationMessage createImageConversationMessage(String url, String thumbnail, String format,
			String orientation, User sender, String conversationId, String application, Boolean ignored) throws InvalidMessageException {
		return new ConversationImageMessage(sender, conversationId, new Date(), url, thumbnail, format, orientation,
				application, ignored);
	}

	public ConversationMessage createVideoConversationMessage(String url, String thumbnail, Long length, String format,
			String orientation, User sender, String conversationId, String application, Boolean ignored) throws InvalidMessageException {
		return new ConversationVideoMessage(sender, conversationId, new Date(), url, thumbnail, length, format,
				orientation, application, ignored);
	}
}
