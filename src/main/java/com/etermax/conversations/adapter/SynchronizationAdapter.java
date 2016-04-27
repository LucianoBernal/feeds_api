package com.etermax.conversations.adapter;

import com.etermax.conversations.dto.HistoryDTO;
import com.etermax.conversations.dto.SyncDTO;

import java.util.List;

public interface SynchronizationAdapter {
	HistoryDTO getConversationHistory(String conversationId, Long firstDate, Long lastDate, Long userId,
			String application);

	List<SyncDTO> getConversationSync(Long userId, String dateString, String application);
}
