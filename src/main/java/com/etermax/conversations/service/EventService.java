package com.etermax.conversations.service;

import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.Event;

public interface EventService {

	void registerEvent(Event event) throws ModelException;

}
