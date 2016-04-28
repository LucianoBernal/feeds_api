package com.etermax.conversations.adapter.impl;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.HistoryDTO;
import com.etermax.conversations.dto.SyncDTO;
import com.etermax.conversations.error.*;
import com.etermax.conversations.model.ConversationHistory;
import com.etermax.conversations.model.ConversationSync;
import com.etermax.conversations.model.Range;
import com.etermax.conversations.service.SynchronizationService;

import java.util.ArrayList;
import java.util.List;

public class SynchronizationAdapterImpl implements SynchronizationAdapter {
	private SynchronizationService synchronizationService;

	public SynchronizationAdapterImpl(SynchronizationService synchronizationService) {
		this.synchronizationService = synchronizationService;
	}

	@Override
	public HistoryDTO getConversationHistory(String conversationId, Long firstDate, Long lastDate, Long userId,
			String application) throws ClientException {
		if (conversationId == null) {
			throw new ClientException(new InvalidConversationIdException(), 400);
		}
		if (userId == null) {
			throw new ClientException(new InvalidUserIdException(), 400);
		}
		if (application == null) {
			throw new ClientException(new InvalidAppException(), 400);
		}
		try {
			Range range = new Range(firstDate, lastDate);
			ConversationHistory conversationHistory = synchronizationService
					.getConversationHistory(conversationId, range, userId, application);
			return new HistoryDTO(conversationHistory);
		} catch (GetConversationMessagesException | InvalidRangeException e) {
			throw new ClientException(e, 400);
		}
	}

	@Override
	public List<SyncDTO> getConversationSync(Long userId, String dateString, String application) {
		List<SyncDTO> displayData = new ArrayList<>();
		if (userId == null) {
			throw new ClientException(new Exception("User id cannot be null"), 400);
		}
		try {
			List<ConversationSync> userData = synchronizationService.getConversationSync(userId, dateString, application);
			userData.forEach(userDataSync -> displayData.add(new SyncDTO(userDataSync)));
			return displayData;
		}catch (GetUserDataException e){
			throw new ClientException(e, 400);
		}
	}
}
