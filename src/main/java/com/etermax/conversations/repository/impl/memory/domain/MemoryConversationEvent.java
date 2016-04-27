package com.etermax.conversations.repository.impl.memory.domain;

import com.etermax.conversations.model.Event;
import com.etermax.conversations.model.EventData;
import com.etermax.conversations.repository.impl.memory.filter.ConversationDataFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryConversationEvent implements MemoryConversationData {

	private final String id;
	private final String conversationId;
	private final Date date;
	private final String eventType;
	private final String application;
	private final List<MemoryEventData> eventDataList;
	private Long userId;

	public MemoryConversationEvent(Event event) {
		this.id = event.getId();
		this.conversationId = event.getConversationId();
		this.date = event.getDate();
		this.eventType = event.getKey();
		this.eventDataList = new ArrayList<>(
				event.getEventsData().stream().map(this::toMemoryEventData).collect(Collectors.toList()));
		this.userId = event.getUserId();
		this.application = event.getApplication();
	}

	private MemoryEventData toMemoryEventData(EventData eventData) {
		String key = eventData.getKey();
		String value = eventData.getValue();
		return new MemoryEventData(key, value);
	}

	public List<MemoryEventData> getEventDataList() {
		return eventDataList;
	}

	public String getEventType() {
		return eventType;
	}

	public String getApplication() { return application; }

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getConversationId() {
		return conversationId;
	}

	@Override
	public String getType() {
		return "event";
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public boolean accept(ConversationDataFilter conversationDataFilter) {
		return conversationDataFilter.filter(this);
	}

	public void addEventData(String key, String value) {
		MemoryEventData memoryEventData = new MemoryEventData(key, value);
		eventDataList.add(memoryEventData);
	}

	public Long getUserId() {
		return userId;
	}
}
