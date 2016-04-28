package com.etermax.conversations.repository.impl.memory;

import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.error.InvalidEventException;
import com.etermax.conversations.error.MessageNotFoundException;
import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.*;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.repository.impl.memory.domain.*;
import com.etermax.conversations.repository.impl.memory.filter.*;
import com.etermax.conversations.repository.impl.memory.mapper.MemoryRepositoryMapper;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoryConversationRepository implements ConversationRepository {

	private List<Conversation> conversations;
	private Map<String, List<MemoryConversationData>> conversationUserData;
	private Long conversationId;
	private Integer maxConversations;
	private Integer maxSyncSize;
	private List<MemoryUnreadMessages> conversationUserUnreadMessages;
	private Map<String, Long> conversationMessageId;
	private long eventsIds;
	private List<MemoryConversationDeletion> conversationDeletionDates;
	private MemoryRepositoryMapper memoryMapper;
	private ConversationComparator conversationComparator;

	public MemoryConversationRepository(Integer maxConversations,
			Integer maxSyncSize) {
		this.maxConversations = maxConversations;
		this.maxSyncSize = maxSyncSize;
		conversations = new ArrayList<>();
		conversationUserData = new HashMap<>();
		conversationId = 1l;
		conversationComparator = new ConversationComparator();
		conversationUserUnreadMessages = new ArrayList<>();
		conversationMessageId = new HashMap<>();
		this.eventsIds = 0l;
		this.conversationDeletionDates = new ArrayList<>();
		memoryMapper = new MemoryRepositoryMapper();

	}

	@Override
	public Conversation saveConversation(Conversation conversation) {
		this.conversations.add(conversation);
		conversation.setId(createConversationId(conversation));
		conversationUserData.put(String.valueOf(conversation.getId()), new ArrayList<>());
		conversationMessageId.put(String.valueOf(conversation.getId()), 1l);
		return conversation;
	}

	@Override
	public List<Conversation> saveConversations(List<Conversation> conversations) {
		return conversations.stream().map(this::saveConversation).collect(Collectors.toList());
	}

	@Override
	public ConversationMessage saveMessage(ConversationMessage conversationMessage, Conversation conversation) {
		setMessageId(conversationMessage, conversation.getId());
		MemoryConversationMessage memoryConversationMessage = memoryMapper
				.toMemoryConversationMessage(conversationMessage, conversation);
		saveMessageIntoConversation(memoryConversationMessage, conversation);
		conversation.setLastUpdated(conversationMessage.getDate());
		conversations.sort(conversationComparator);
		incrementUnreadMessages(conversationMessage, conversation.getId(), conversation);
		return conversationMessage;
	}

	private void setMessageId(ConversationMessage conversationMessage, String conversationId) {
		Long messageId = conversationMessageId.get(conversationId);
		conversationMessage.setId(messageId.toString());
		conversationMessageId.put(conversationId, messageId + 1l);
	}

	@Override
	public Conversation getConversationWithId(String conversationId) throws ConversationNotFoundException {
		return filterConversationsWithId(conversationId, conversations);
	}

	@Override
	public Conversation getConversationWithUsers(Set<Long> userIds) throws ConversationNotFoundException {
		Stream<Conversation> stream = conversations.stream();
		Stream<Conversation> conversationStream = stream.filter(getConversationWithUsersPredicate(userIds));
		List<Conversation> conversations = toList(conversationStream);
		return checkConversationExists(conversations);
	}

	@Override
	public List<Conversation> getUserConversations(Long userId) {
		return conversations.stream().filter(getConversationPredicate(userId)).collect(Collectors.toList());
	}

	@Override
	public List<Conversation> getUserActiveConversations(Long userId, String application) {
		return conversations.stream().filter(getConversationPredicate(userId)).collect(Collectors.toList());
	}

	private List<ConversationMessage> getUserMessages(Date date,
			List<MemoryConversationData> memoryConversationDataList, Long userId, String app) {
		FilterChainResponsibility filterChainResponsibility = buildRetrocompatibilityFilterChainResponsibility(userId,
				date, app);
		List<MemoryConversationData> filteredData = filterChainResponsibility.filter(memoryConversationDataList);
		List<MemoryConversationMessage> filteredMemoryMessages = filteredData.stream()
				.map(data -> (MemoryConversationMessage) data).collect(Collectors.toList());
		return memoryMapper.toConversationMessages(filteredMemoryMessages);
	}

	private FilterChainResponsibility buildRetrocompatibilityFilterChainResponsibility(Long userId, Date date,
			String app) {
		FilterChainResponsibility chain = new FilterChainResponsibility();
		chain.addFilter(new DateConversationDataFilter(date));
		chain.addFilter(new ApplicationDataFilter(app));
		chain.addFilter(new DeletedMessagesDataFilter(userId));
		chain.addFilter(new RetrocompatibilityDataFilter());
		chain.addFilter(new IgnoredMessageDataFilter(userId));
		return chain;
	}

	@Override
	public void deleteMessage(String conversationId, String messageId, Long userId) {
		MemoryConversationMessage message = getMessage(conversationId, messageId);
		message.delete(userId);
	}

	@Override
	public ConversationHistory getConversationHistory(String conversationId, Range range, Long userId,
			String application) {
		return getUserHistory(conversationId, range, userId, application);
	}

	@Override
	public void deleteConversation(String conversationId, Long userId, Date deletionDate, String app) {
		resetRead(conversationId, app, userId);
		setLastConversationDeletionDateForUser(conversationId, deletionDate, userId, app);
	}

	private void setLastConversationDeletionDateForUser(String conversationId, Date deletionDate, Long userId,
			String app) {
		MemoryConversationDeletion memoryConversationDeletion = new MemoryConversationDeletion(conversationId, userId,
				app, deletionDate);
		conversationDeletionDates.removeIf(
				deletion -> deletion.getConversationId().equals(conversationId) && deletion.getUserId().equals(userId)
						&& deletion.getApp().equals(app));
		conversationDeletionDates.add(memoryConversationDeletion);
	}

	@Override
	public ConversationMessage getConversationMessage(String conversationId, String messageId, Long userId) {
		return memoryMapper.toConversationMessage(getConversationMessageById(conversationId, messageId));
	}

	@Override
	public void resetRead(String conversationId, String app, Long userId) {
		MemoryUnreadMessages memoryUnreadMessages = getConversationUnreadMessages(conversationId, app);
		memoryUnreadMessages.setUserUnreadMessages(userId, 0l);
	}

	@Override
	public IndividualMessageReceipt saveReceiptInMessage(String conversationId, String messageId,
			IndividualMessageReceipt receipt) {
		List<MemoryConversationData> conversationDataList = conversationUserData.get(conversationId);
		MemoryConversationMessage memoryConversationMessage = (MemoryConversationMessage) conversationDataList.stream()
				.filter(conversationData -> conversationData.getId().equals(messageId) && conversationData
						.getConversationId().equals(conversationId)).collect(Collectors.toList()).get(0);
		memoryConversationMessage.addIndividualReceipt(receipt);

		Long receiptSender = receipt.getUser();
		String application = getConversationMessage(conversationId, messageId, receiptSender).getApplication();
		ReadResetter readResetter = new ReadResetter(conversationId, application, receiptSender, this);
		receipt.getType().accept(readResetter);
		return receipt;

	}

	@Override
	public void saveEvent(Event event) throws ModelException {
		if (invalidEventKey(event) || invalidEventValue(event)) {
			throw new ModelException(new InvalidEventException());
		}
		Long eventId = this.eventsIds++;
		event.setId(eventId.toString());
		MemoryConversationEvent memoryConversationEvent = new MemoryConversationEvent(event);
		if (event.getKey().equals("DELETE_MESSAGE")) {
			String messageId = event.getEventsData().get(0).getValue();
			Date date = conversationUserData.get(event.getConversationId()).stream()
					.filter(elem -> elem.getType().equals("message") && elem.getId().equals(messageId))
					.collect(Collectors.toList()).get(0).getDate();
			String messageDate = String.valueOf(date.getTime());
			memoryConversationEvent.addEventData("messageDate", messageDate);
		}
		conversationUserData.get(event.getConversationId()).add(memoryConversationEvent);

	}

	@Override
	public List<ConversationSync> getConversationSyncData(Long userId, List<String> conversationIds, Date date,
			String application) {
		List<ConversationSync> conversationSyncs = new ArrayList<>();
		conversationIds = conversationIds.subList(0, Math.min(maxConversations, conversationIds.size()));
		for (String conversationId : conversationIds) {
			List<ConversationData> conversationDataList = getUserConversationData(userId, date, conversationId,
					application);
			if (conversationDataList.size() != 0) {
				int toIndex = conversationDataList.size();
				int fromIndex = toIndex - Math.min(conversationDataList.size(), maxSyncSize);
				List<ConversationData> conversationSyncData = conversationDataList.subList(fromIndex, toIndex);
				Integer dataLeft = conversationDataList.size() - conversationSyncData.size();
				Date hasMoreLastDate;
				Date hasMoreFirstDate;
				if (dataLeft.equals(0)) {
					hasMoreFirstDate = new Date(0);
					hasMoreLastDate = new Date(0);
				} else {
					hasMoreLastDate = conversationDataList.get(dataLeft - 1).getDate();
					hasMoreFirstDate = conversationDataList.get(0).getDate();
				}
				HasMore hasMore = new HasMore(dataLeft, hasMoreFirstDate, hasMoreLastDate);
				ConversationSync conversationSync = createConversationDataSync(String.valueOf(conversationId), conversationSyncData,
						hasMore, userId, application);
				conversationSyncs.add(conversationSync);
			}
		}
		return conversationSyncs;
	}

	@Override
	public void clearRepository() {

	}

	@Override
	public void flush() {

	}

	@Override
	public List<ConversationMessage> saveMessages(List<ConversationMessage> messages) {

		Map<String, List<Conversation>> conversations = getConversations(messages);

		return messages.stream().sorted((o1, o2) -> new Long(o1.getDate().getTime()).compareTo(o2.getDate().getTime()))
				.map(message -> saveMessage(message, conversations.get(message.getConversationId()).get(0)))
				.collect(Collectors.toList());
	}

	private Map<String, List<Conversation>> getConversations(List<ConversationMessage> messages) {
		Set<String> conversationIds = messages.stream()
				.collect(Collectors.groupingBy(ConversationMessage::getConversationId)).keySet();

		return conversationIds.stream().map(this::getConversationWithId)
				.collect(Collectors.groupingBy(Conversation::getId));
	}

	@Override
	public boolean isAlreadyAcknowledged(String conversationId, String messageId, IndividualMessageReceipt receipt) {
		MemoryConversationMessage memoryConversationMessage = getConversationMessageById(conversationId, messageId);
		return memoryConversationMessage.getReceipts().stream().anyMatch(
				r -> r.getUser().equals(receipt.getUser()) && r.getType().toString()
						.equals(receipt.getType().toString()));
	}

	@Override
	public String getMessageApplication(String conversationId, String messageId) {
		return getConversationMessageById(conversationId, messageId).getApplication();
	}

	@Override
	public Map<String, Long> getUnreadMessagesCount(List<String> conversations, Long userId, String application) {
		Map<String, Long> unreadMessagesMap = new HashMap<>();
		for (String conversationId : conversations) {
			Optional<Long> unreadCount = conversationUserUnreadMessages.stream()
					.filter(memoryUnreadMessage -> memoryUnreadMessage.getApplication().equals(application)
							&& memoryUnreadMessage.getConversationId().equals(conversationId))
					.map(mum -> mum.getUserUnreadMessages(userId)).findFirst();
			unreadMessagesMap.put(conversationId, unreadCount.orElse(0l));
		}
		return unreadMessagesMap;
	}

	@Override
	public String createConversationId(Conversation conversation) {
		if(conversation.getId() == null){
			return String.valueOf(conversationId++);
		}else{
			return conversation.getId();
		}
	}

	private ConversationSync createConversationDataSync(String conversationId,
			List<ConversationData> conversationDataList, HasMore hasMore, Long userId, String application) {
		Long unreadMessages = getConversationUnreadMessages(conversationId, application).getUserUnreadMessages(userId);
		return new ConversationSync(conversationId, conversationDataList, hasMore, unreadMessages);
	}

	private List<ConversationData> getUserConversationData(Long userId, Date date, String conversationId,
			String application) {
		try {
			if (conversationUserData.get(conversationId) == null || conversationUserData.get(conversationId)
					.isEmpty()) {
				return Collections.<ConversationData>emptyList();
			}
			Date lastDeletedDate = getLastDeletedDate(conversationId, userId, application);
			Date actualSyncDate = new Date(Math.max(lastDeletedDate.getTime(), date.getTime()));
			FilterChainResponsibility chain = buildSyncFilterChainResponsibility(userId, actualSyncDate, application);
			List<MemoryConversationData> filteredData = chain.filter(conversationUserData.get(conversationId)).stream()
					.map(data -> filterUserReceipts(data, userId))
					.map(data -> getReceiptsFromMessages(data, actualSyncDate)).collect(Collectors.toList());
			return memoryMapper.toConversationData(filteredData);
		} catch (Exception e) {
			throw new ModelException(e);
		}

	}

	private MemoryConversationData filterUserReceipts(MemoryConversationData data, Long userId) {
		if (data.getType().equals("message")) {
			MemoryConversationMessage message = (MemoryConversationMessage) data;
			if (!message.getConversationMessage().getSender().getId().equals(userId)) {
				return new MemoryConversationMessage(message.getConversationMessage());
			} else {
				return data;
			}
		} else {
			return data;
		}
	}

	private MemoryConversationData getReceiptsFromMessages(MemoryConversationData data, Date actualSyncDate) {
		if (messageAlreadySynced(data, actualSyncDate)) {
			MemoryConversationMessage message = (MemoryConversationMessage) data;
			String application = message.getApplication();
			List<IndividualMessageReceipt> receipts = message.getReceipts();
			List<IndividualMessageReceipt> receiptsToSync = receipts.stream()
					.filter(receipt -> receipt.getDate().compareTo(actualSyncDate) > 0).collect(Collectors.toList());
			return new MemoryMessageReceipt(message.getId(), receiptsToSync, message.getConversationId(), application);
		} else {
			return data;
		}

	}

	private boolean messageAlreadySynced(MemoryConversationData data, Date actualSyncDate) {
		return data.getType().equals("message") && data.getDate().compareTo(actualSyncDate) <= 0;
	}

	private FilterChainResponsibility buildSyncFilterChainResponsibility(Long userId, Date actualSyncDate,
			String application) {
		FilterChainResponsibility chain = new FilterChainResponsibility();
		chain.addFilter(new DateConversationDataFilter(actualSyncDate));
		chain.addFilter(new ApplicationDataFilter(application));
		chain.addFilter(new IgnoredMessageDataFilter(userId));
		chain.addFilter(new DeletedMessagesDataFilter(userId));
		chain.addFilter(new UserEventsDataFilter(userId));
		chain.addFilter(new DeletetionEventDataFilter(actualSyncDate));
		return chain;
	}

	private Date getLastDeletedDate(String conversationId, Long userId, String app) {
		List<MemoryConversationDeletion> memoryConversationDeletionList = conversationDeletionDates.stream()
				.filter(deletion -> deletion.getConversationId().equals(conversationId) && deletion.getUserId()
						.equals(userId) && deletion.getApp().equals(app)).collect(Collectors.toList());
		return (memoryConversationDeletionList.isEmpty()) ?
				new Date(0) :
				memoryConversationDeletionList.get(0).getDeletionDate();

	}

	private boolean invalidEventValue(Event event) {
		List<EventData> eventsData = event.getEventsData();
		return eventsData.stream().anyMatch(eventData -> eventData.getValue().isEmpty());
	}

	private boolean invalidEventKey(Event event) {
		List<EventData> eventsData = event.getEventsData();
		return eventsData.stream().anyMatch(eventData -> eventData.getKey().isEmpty());
	}

	private MemoryUnreadMessages getConversationUnreadMessages(String conversationId, String app) {
		List<MemoryUnreadMessages> unreadMessages = conversationUserUnreadMessages.stream()
				.filter(unread -> unread.getConversationId().equals(conversationId) && unread.getApplication()
						.equals(app)).collect(Collectors.toList());
		if (unreadMessages.isEmpty()) {
			return new MemoryUnreadMessages(conversationId, app);
		} else {
			return unreadMessages.get(0);
		}
	}

	private MemoryConversationMessage getConversationMessageById(String conversationId, String messageId)
			throws MessageNotFoundException {
		if (conversationUserData.get(conversationId) == null) {
			throw new MessageNotFoundException();
		}
		List<MemoryConversationData> filteredMessages = conversationUserData.get(conversationId).stream()
				.filter(conversationMessage -> conversationMessage.getId().equals(messageId) && conversationMessage
						.getType().equals("message")).collect(Collectors.toList());
		if (filteredMessages.isEmpty()) {
			throw new MessageNotFoundException();
		} else {
			return (MemoryConversationMessage) filteredMessages.get(0);
		}
	}

	private Conversation checkConversationExists(List<Conversation> conversations)
			throws ConversationNotFoundException {
		if (conversations.isEmpty()) {
			throw new ConversationNotFoundException();
		} else {
			//fixme revisar esto cuando haya grupos
			return conversations.get(0);
		}
	}

	private MemoryConversationMessage getMessage(String conversationId, String messageId)
			throws MessageNotFoundException {
		if (!conversationUserData.containsKey(conversationId)) {
			throw new MessageNotFoundException();
		}
		List<MemoryConversationData> memoryConversationData = conversationUserData.get(conversationId).stream()
				.filter(elem -> elem.getType().equals("message") && elem.getId().equals(messageId))
				.collect(Collectors.toList());
		if (memoryConversationData.isEmpty()) {
			throw new MessageNotFoundException();
		}
		return (MemoryConversationMessage) memoryConversationData.get(0);
	}

	private ConversationHistory getUserHistory(String conversationId, Range range, Long userId, String application) {
		if (conversationUserData == null || conversationUserData.isEmpty()) {
			return new ConversationHistory(new ArrayList<>(), new HasMore(0, new Date(0), new Date(0)));
		}
		List<ConversationData> conversationDataList = getHistoryConversationData(conversationId, range, userId,
				application);
		HasMore hasMore = new HasMore(0, new Date(0), new Date(0));
		List<ConversationData> conversationHistoryData = new ArrayList<>();
		if (!(conversationDataList.size() == 0)) {
			int toIndex = conversationDataList.size();
			int fromIndex = toIndex - Math.min(conversationDataList.size(), maxSyncSize);
			conversationHistoryData = conversationDataList.subList(fromIndex, toIndex);
			Integer dataLeft = conversationDataList.size() - conversationHistoryData.size();
			if (!dataLeft.equals(0)) {
				Date hasMoreFirstDate = conversationDataList.get(0).getDate();
				Date hasMoreLastDate = conversationDataList.get(dataLeft - 1).getDate();
				hasMore = new HasMore(dataLeft, hasMoreFirstDate, hasMoreLastDate);
			}
		}
		return createConversationHistory(Lists.reverse(conversationHistoryData), hasMore);
	}

	private List<ConversationData> getHistoryConversationData(String conversationId, Range range, Long userId,
			String app) {
		Date lastDeletedDate = getLastDeletedDate(conversationId, userId, app);
		if (lastDeletedDate != null && (range.getFirstDate() == null || lastDeletedDate.getTime() > range
				.getFirstDate())) {
			range = new Range(lastDeletedDate.getTime(), range.getLastDate());
		}
		List<MemoryConversationData> memoryConversationDataList = conversationUserData.get(conversationId);
		FilterChainResponsibility filterChainResponsibility = buildHistoryFilterChainResponsibility(userId, range, app);
		Date actualSyncDate = new Date(range.getFirstDate());
		List<MemoryConversationData> filteredData = filterChainResponsibility.filter(memoryConversationDataList)
				.stream().map(data -> filterUserReceipts(data, userId))
				.map(data -> getReceiptsFromMessages(data, actualSyncDate)).collect(Collectors.toList());
		return memoryMapper.toConversationData(filteredData);
	}

	private ConversationHistory createConversationHistory(List<ConversationData> conversationHistoryData,
			HasMore hasMore) {
		return new ConversationHistory(conversationHistoryData, hasMore);
	}

	private FilterChainResponsibility buildHistoryFilterChainResponsibility(Long userId, Range range, String app) {
		FilterChainResponsibility chain = new FilterChainResponsibility();
		chain.addFilter(new IsInRangeDataFilter(range));
		chain.addFilter(new ApplicationDataFilter(app));
		chain.addFilter(new IgnoredMessageDataFilter(userId));
		chain.addFilter(new DeletedMessagesDataFilter(userId));
		chain.addFilter(new UserEventsDataFilter(userId));
		Long firstDate = range.getFirstDate();
		firstDate = firstDate == null ? 0 : firstDate;
		Date date = new Date(firstDate);
		chain.addFilter(new DeletetionEventDataFilter(date));
		return chain;
	}

	private Predicate<Conversation> getConversationPredicate(Long userId) {
		return conversation -> {
			Set<User> users = conversation.getUsers();
			Set<Long> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
			return userIds.contains(userId);
		};
	}

	private Conversation filterConversationsWithId(String conversationId, List<Conversation> conversations)
			throws ConversationNotFoundException {
		Stream<Conversation> stream = conversations.stream();
		List<Conversation> filteredConversations = toList(
				stream.filter(getConversationWithIdPredicate(conversationId)));
		return checkConversationExists(filteredConversations);
	}

	private Predicate<Conversation> getConversationWithUsersPredicate(Set<Long> userIds) {
		return conversation -> getIds(conversation).equals(userIds);
	}

	private Set<Long> getIds(Conversation conversation) {
		return conversation.getUsers().stream().map(User::getId).collect(Collectors.toSet());
	}

	private Predicate<Conversation> getConversationWithIdPredicate(String conversationId) {
		return conversation -> conversation.getId().equals(conversationId);
	}

	private List<Conversation> toList(Stream<Conversation> conversationStream) {
		return conversationStream.collect(Collectors.toList());
	}

	private void saveMessageIntoConversation(MemoryConversationMessage conversationMessage, Conversation conversation) {
		String conversationId = conversation.getId();
		List<MemoryConversationData> conversationDataList = conversationUserData.get(conversationId);
		if (conversationDataList == null) {
			conversationDataList = new ArrayList<>();
		}
		conversationDataList.add(conversationMessage);
		conversationUserData.put(conversationId, conversationDataList);
	}

	private void incrementUnreadMessages(ConversationMessage conversationMessage, String conversationId,
			Conversation conversation) {
		String app = conversationMessage.getApplication();
		MemoryUnreadMessages unreadMessages = getConversationUnreadMessages(conversationId, app);
		List<Long> receivers = conversation.getUserIds().stream()
				.filter(user -> !user.equals(conversationMessage.getSender().getId())).collect(Collectors.toList());
		for (Long receiverId : receivers) {
			Long unread = unreadMessages.getUserUnreadMessages(receiverId);
			unreadMessages.setUserUnreadMessages(receiverId, unread + 1);
		}
		conversationUserUnreadMessages.add(unreadMessages);
	}

}
