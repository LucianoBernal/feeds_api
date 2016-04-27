package com.etermax.conversations.repository.impl.memory.mapper;

import com.etermax.conversations.model.*;
import com.etermax.conversations.repository.impl.memory.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MemoryRepositoryMapper {
	public List<ConversationData> toConversationData(List<MemoryConversationData> filteredData) {
		List<ConversationData> conversationDataList = new ArrayList<>();
		filteredData.forEach(
				memoryConversationData -> conversationDataList.add(buildConversationData(memoryConversationData)));
		return conversationDataList;
	}

	private ConversationData buildConversationData(MemoryConversationData memoryConversationData) {
		if (memoryConversationData.getType().equals("message")) {
			return createConversationMessage((MemoryConversationMessage) memoryConversationData);
		} else if (memoryConversationData.getType().equals("receipt")) {
			return createMessageReceipt((MemoryMessageReceipt) memoryConversationData);
		} else {
			return createConversationEvent((MemoryConversationEvent) memoryConversationData);
		}
	}

	private ConversationData createConversationEvent(MemoryConversationEvent memoryConversationEvent) {
		Date date = memoryConversationEvent.getDate();
		List<MemoryEventData> memoryEventDataList = memoryConversationEvent.getEventDataList();
		List<EventData> eventData = memoryEventDataList.stream().map(this::buildEventData).collect(Collectors.toList());
		String conversationId = memoryConversationEvent.getConversationId();
		Long userId = memoryConversationEvent.getUserId();
		String app = memoryConversationEvent.getApplication();
		return new Event(memoryConversationEvent.getEventType(), eventData, conversationId, userId, date, app);
	}

	private ConversationData createConversationMessage(MemoryConversationMessage memoryConversationMessage) {
		ConversationMessage conversationMessage = memoryConversationMessage.getConversationMessage();
		List<IndividualMessageReceipt> receipts = memoryConversationMessage.getReceipts();
		String application = memoryConversationMessage.getApplication();
		if (!receipts.isEmpty()) {
			MessageReceipt messageReceipt = new MessageReceipt(conversationMessage.getId(), receipts,
					conversationMessage.getConversationId(), application);
			conversationMessage.addMessageReceipt(messageReceipt);
		}
		return conversationMessage;
	}

	private ConversationData createMessageReceipt(MemoryMessageReceipt memoryReceipt) {
		String messageId = memoryReceipt.getId();
		List<IndividualMessageReceipt> receipts = memoryReceipt.getReceipts();
		String conversationId = memoryReceipt.getConversationId();
		String application = memoryReceipt.getApplication();
		return new MessageReceipt(messageId, receipts, conversationId, application);
	}

	private EventData buildEventData(MemoryEventData memoryEventData) {
		return new EventData(memoryEventData.getKey(), memoryEventData.getValue());
	}

	public ConversationMessage toConversationMessage(MemoryConversationMessage memoryConversationMessage) {
		return memoryConversationMessage.getConversationMessage();
	}

	public List<ConversationMessage> toConversationMessages(List<MemoryConversationMessage> filteredMemoryMessages) {
		return filteredMemoryMessages.stream().map(MemoryConversationMessage::getConversationMessage)
				.collect(Collectors.toList());
	}

	public MemoryConversationMessage toMemoryConversationMessage(ConversationMessage conversationMessage,
			Conversation conversation) {
		return setIgnoredBy(conversationMessage, conversation);
	}

	private MemoryConversationMessage setIgnoredBy(ConversationMessage conversationMessage,
			Conversation conversation) {
		MemoryConversationMessage memoryConversationMessage = new MemoryConversationMessage(conversationMessage);
		Set<Long> ignoredBy = conversation.getUserIds().stream()
				.filter(id -> conversationMessage.getIgnored() && id != memoryConversationMessage.getConversationMessage().getSender().getId().longValue())
				.collect(Collectors.toSet());
		memoryConversationMessage.setIgnoredBy(ignoredBy);
		return memoryConversationMessage;
	}
}
