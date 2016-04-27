package com.etermax.conversations.service.impl;

import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.Event;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.EventService;

public class EventServiceImpl implements EventService {

	ConversationRepository repository;

	public EventServiceImpl(ConversationRepository conversationRepository) {
		this.repository = conversationRepository;
	}

	public void registerEvent(Event event) throws ModelException {
		repository.saveEvent(event);
	}

}
