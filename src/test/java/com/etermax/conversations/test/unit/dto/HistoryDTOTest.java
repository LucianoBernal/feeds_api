package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.dto.HistoryDTO;
import com.etermax.conversations.dto.TextMessageDisplayDTO;
import com.etermax.conversations.error.InvalidMessageException;
import com.etermax.conversations.error.InvalidUserException;
import com.etermax.conversations.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HistoryDTOTest {

	@Test
	public void conversationHistoryDisplayTest() throws InvalidUserException, InvalidMessageException {
		//Given
		List<ConversationData> conversationData = new ArrayList<>();
		ConversationTextMessage conversationTextMessage = new ConversationTextMessage(new User(1l), "1", new Date(),
				"bla", "A2", false);
		conversationData.add(conversationTextMessage);
		HasMore hasMore = new HasMore(0, new Date(1l), new Date(2l));
		ConversationHistory conversationHistory = new ConversationHistory(conversationData, hasMore);

		//When
		HistoryDTO historyDTO = new HistoryDTO(
				conversationHistory);

		//Then
		assertThat(historyDTO.getHasMore().getHasMore()).isEqualTo(false);
		assertThat(historyDTO.getHasMore().getTotalMessages()).isEqualTo(0);
		assertThat(historyDTO.getConversationDataDTO().size()).isEqualTo(1);
		ConversationDataDTO conversationDataDTO = historyDTO.getConversationDataDTO().get(0);
		assertThat(conversationDataDTO).isInstanceOf(TextMessageDisplayDTO.class);
		TextMessageDisplayDTO messageDisplayDTO = (TextMessageDisplayDTO) conversationDataDTO;
		assertThat(messageDisplayDTO.getText()).isEqualTo("bla");
		assertThat(messageDisplayDTO.getSenderId()).isEqualTo(1l);
	}
}
