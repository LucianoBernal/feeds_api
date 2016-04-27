package com.etermax.conversations.dto;

import com.etermax.conversations.model.Event;
import com.etermax.conversations.model.EventData;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EventDTO implements ConversationDataDTO {

	@JsonProperty("date") private Object date;

	@JsonProperty("key") private String key;

	@JsonProperty("events_data") private List<EventData> eventsData;

	@JsonProperty("type") private String type;

	public EventDTO(Event event) {
		this.date = event.getDate();
		this.key = event.getKey();
		this.eventsData = event.getEventsData();
		this.type = "event";
	}

	public Object getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<EventData> getEventsData() {
		return eventsData;
	}

	@Override
	public String getType() {
		return this.type;
	}
}
