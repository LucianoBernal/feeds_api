package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.model.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConversationMessageMapperVisitor {
	private Conversation conversation;

	public ConversationMessageMapperVisitor(Conversation conversation) {
		this.conversation = conversation;
	}

	public ElasticSearchMessage visit(ConversationAudioMessage conversationAudioMessage) {
		return new ElasticSearchAudioMessage(conversationAudioMessage.getId(),
				conversationAudioMessage.getSender().getId(), conversationAudioMessage.getUrl(),
				conversationAudioMessage.getFormat(), conversationAudioMessage.getLength(),
				conversationAudioMessage.getDate().getTime(), conversationAudioMessage.getApplication(),
				getIgnoredBy(conversationAudioMessage));
	}

	public ElasticSearchMessage visit(ConversationImageMessage conversationImageMessage) {
		return new ElasticSearchImageMessage(conversationImageMessage.getId(),
				conversationImageMessage.getSender().getId(), conversationImageMessage.getUrl(),
				conversationImageMessage.getThumbnail(), conversationImageMessage.getFormat(),
				conversationImageMessage.getOrientation(), conversationImageMessage.getDate().getTime(),
				conversationImageMessage.getApplication(), getIgnoredBy(conversationImageMessage));
	}

	public ElasticSearchMessage visit(ConversationTextMessage conversationTextMessage) {
		return new ElasticSearchTextMessage(conversationTextMessage.getId(),
				conversationTextMessage.getSender().getId(), conversationTextMessage.getText(),
				conversationTextMessage.getDate().getTime(), conversationTextMessage.getApplication(),
				getIgnoredBy(conversationTextMessage));
	}

	public ElasticSearchMessage visit(ConversationVideoMessage conversationVideoMessage) {
		return new ElasticSearchVideoMessage(conversationVideoMessage.getId(),
				conversationVideoMessage.getSender().getId(), conversationVideoMessage.getUrl(),
				conversationVideoMessage.getThumbnail(), conversationVideoMessage.getLength(),
				conversationVideoMessage.getFormat(), conversationVideoMessage.getOrientation(),
				conversationVideoMessage.getDate().getTime(), conversationVideoMessage.getApplication(),
				getIgnoredBy(conversationVideoMessage));
	}

	public ElasticSearchEvent visit(Event event) {
		return new ElasticSearchEvent(event.getConversationId(), event.getId(), event.getDate(), event.getKey(),
				event.getEventsData(), event.getUserId(), event.getApplication());
	}

	private Set<Long> getIgnoredBy(ConversationMessage conversationMessage) {
		return !conversationMessage.getIgnored() ?
				new HashSet<>() :
				conversation.getUserIds().stream().filter(id -> !id.equals(conversationMessage.getSender().getId()))
						.collect(Collectors.toSet());
	}
}
