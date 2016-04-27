package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.SyncDTO;
import com.etermax.conversations.dto.TextMessageDisplayDTO;
import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SyncDTOTest {

	@Test
	public void conversationSyncDTOTest() throws ModelException {
		//Given
		List<ConversationData> conversationData = new ArrayList<>();
		ConversationTextMessage conversationTextMessage = new ConversationTextMessage(new User(1l), "1", new Date(), "bla",
				"A2", false);
		conversationData.add(conversationTextMessage);
		HasMore hasMore = new HasMore(0, new Date(1l), new Date(2l));
		ConversationSync conversationSync = new ConversationSync("1", conversationData, hasMore, 2l);
		//When
		SyncDTO syncDTO = new SyncDTO(conversationSync);

		//Then
		assertThat(syncDTO.getConversationId()).isEqualTo("1");
		assertThat(syncDTO.getHasMore().getHasMore()).isEqualTo(false);
		assertThat(syncDTO.getConversationData().size()).isEqualTo(1);
		TextMessageDisplayDTO messageConversationDisplayDTO = (TextMessageDisplayDTO) syncDTO.getConversationData()
				.get(0);
		assertThat(messageConversationDisplayDTO.getText()).isEqualTo("bla");
		assertThat(syncDTO.getUnreadMessages()).isEqualTo(2l);
	}
}
