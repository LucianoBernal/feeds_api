package com.etermax.conversations.test.unit.model;

import com.etermax.conversations.model.Event;
import com.etermax.conversations.model.EventData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

	@Test
	public void eventCreationTest() {
		//GIVEN
		List<EventData> eventsData = givenAEventDataList();
		eventsData.add(new EventData("id", "654654"));
		String conversationId = "1";
		Long userId = 123l;
		Date date = new Date();
		Long eventId = 1l;
		String application = "A2";

		//WHEN
		Event deleteMessageEvent = new Event("DELETE_MESSAGE", eventsData, conversationId, userId, date, application);

		//THEN
		assertThat(deleteMessageEvent).isNotNull().isInstanceOf(Event.class);
		assertThat(deleteMessageEvent.getDate()).isEqualTo(date);
		assertThat(deleteMessageEvent.getConversationId()).isEqualTo("1");
		assertThat(deleteMessageEvent.getUserId()).isEqualTo(123l);
		assertThat(deleteMessageEvent.getKey()).isEqualTo("DELETE_MESSAGE");
		assertThat(deleteMessageEvent.getEventsData().get(0)).isInstanceOf(EventData.class);

	}

	@Test
	public void eventDataTest() {
		//GIVEN
		List<EventData> eventsData = givenAEventDataList();

		//WHEN
		eventsData.add(new EventData("id", "654654"));

		//THEN
		assertThat(eventsData).extracting("key").containsOnly("id");
		assertThat(eventsData).extracting("value").containsOnly("654654");
	}

	private List<EventData> givenAEventDataList() {
		List<EventData> eventsDataList = new ArrayList<>();
		return eventsDataList;
	}
}
