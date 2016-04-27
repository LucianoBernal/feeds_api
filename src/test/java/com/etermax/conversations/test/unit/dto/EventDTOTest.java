package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.EventDTO;
import com.etermax.conversations.model.Event;
import com.etermax.conversations.model.EventData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventDTOTest {

	Date date = new Date();
	EventData eventData = new EventData("id", "654654");

	@Test
	public void eventDTOCreationTest() {
		//GIVEN
		Event event = givenADeleteMessageEvent();

		//WHEN
		EventDTO eventDTO = new EventDTO(event);

		//THEN
		assertThat(eventDTO).isInstanceOf(EventDTO.class);
		assertThat(eventDTO.getKey()).isEqualTo("DELETE_MESSAGE");
		assertThat(eventDTO.getDate()).isEqualTo(this.date);
		assertThat(eventDTO.getEventsData()).containsOnly(this.eventData);

	}

	@Test
	public void eventDTOSettersTest() {
		//GIVEN
		Date anotherDate = new Date();
		String anotherKey = "DELETE_CONVERSATION";
		Event event = givenADeleteMessageEvent();

		//WHEN
		EventDTO eventDTO = new EventDTO(event);
		eventDTO.setDate(anotherDate.toString());
		eventDTO.setKey(anotherKey);

		//THEN
		assertThat(eventDTO).isInstanceOf(EventDTO.class);
		assertThat(eventDTO.getKey()).isEqualTo("DELETE_CONVERSATION");
		assertThat(eventDTO.getDate()).isEqualTo(this.date.toString());

	}

	private Event givenADeleteMessageEvent() {
		List<EventData> eventsData = givenAEventDataList();
		String conversationId = "1";
		Long userId = 123l;
		Long eventId = 1l;
		String application = "A2";
		return new Event("DELETE_MESSAGE",eventsData,conversationId,userId,this.date, application);
	}

	private List<EventData> givenAEventDataList() {
		List<EventData> eventsDataList = new ArrayList<>();
		eventsDataList.add(eventData);
		return eventsDataList;
	}
}
