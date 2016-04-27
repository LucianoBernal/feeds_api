package com.etermax.conversations.adapter.impl;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.HistoryDTO;
import com.etermax.conversations.dto.SyncDTO;
import com.etermax.conversations.error.*;
import com.etermax.conversations.model.ConversationHistory;
import com.etermax.conversations.model.ConversationSync;
import com.etermax.conversations.model.Range;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationResult;
import com.etermax.conversations.retrocompatibility.migration.service.MigrationService;
import com.etermax.conversations.service.SynchronizationService;

import javax.management.InvalidApplicationException;
import java.util.ArrayList;
import java.util.List;

public class SynchronizationAdapterImpl implements SynchronizationAdapter {
	private SynchronizationService synchronizationService;
	private MigrationService migrationService;

	public SynchronizationAdapterImpl(SynchronizationService synchronizationService, MigrationService migrationService) {
		this.synchronizationService = synchronizationService;
		this.migrationService = migrationService;
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
			// MIGRATION

			List<MigrationResult> migrationResults = migrationService.migrateConversations(userId);
			if(!migrationResults.isEmpty()){
				Thread.sleep(1000l);
			}
			List<ConversationSync> userData = synchronizationService.getConversationSync(userId, dateString, application);
			userData.forEach(userDataSync -> displayData.add(new SyncDTO(userDataSync)));
			return displayData;
		}catch (GetUserDataException e){
			throw new ClientException(e, 400);
		} catch (InterruptedException e) {
			throw new ServerException(e, "");
		}
	}
}
