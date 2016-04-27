package com.etermax.conversations.repository.impl.elasticsearch.dao;

import com.etermax.conversations.error.ConversationNotFoundException;
import com.etermax.conversations.error.MessageNotFoundException;
import com.etermax.conversations.error.ServerException;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.Range;
import com.etermax.conversations.repository.impl.elasticsearch.domain.*;
import com.etermax.conversations.repository.impl.elasticsearch.mapper.RuntimeTypeAdapterFactory;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import rx.Observable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;

public class ElasticsearchDAO {

	private Gson gson;
	private Client client;
	private String indexName;
	private Integer maxMessages;

	public ElasticsearchDAO(Client client, String indexName, Integer maxMessages) {
		this.client = client;
		this.indexName = indexName;
		this.gson = new GsonBuilder().create();
		RuntimeTypeAdapterFactory<ElasticSearchConversationData> rta = getAdapterFactoryForConversations();
		RuntimeTypeAdapterFactory<ElasticSearchMessage> rtaMessage = getAdapterFactoryForMessages();
		this.gson = new GsonBuilder().registerTypeAdapterFactory(rta).registerTypeAdapterFactory(rtaMessage).create();
		this.maxMessages = maxMessages;
	}

	public ElasticsearchConversation saveConversation(ElasticsearchConversation conversation) {
		client.prepareIndex(indexName, "conversation")
			  .setId(String.valueOf(conversation.getId()))
			  .setRouting(conversation.getId())
			  .setSource(serialize(conversation))
			  .get();
		conversation.getUsers().forEach(user -> updateUserDocument(conversation.getId(), user));
		return conversation;
	}

	public List<ElasticsearchConversation> saveConversations(List<ElasticsearchConversation> conversations) {
		BulkResponse bulkResponse = buildSaveConversationsRequest(conversations).execute().actionGet();
		List<ElasticsearchConversation> response = QueryConversations(conversations, getIdsNotSaved(bulkResponse));
		buildIndexConversationInUsersRequest(response).execute().actionGet();
		return response;
	}

	public ElasticsearchConversation getConversationById(String id) {
		GetResponse response = client.prepareGet(indexName, "conversation", id).setRouting(id).get();
		String source = response.getSourceAsString();
		if (source == null) {
			throw new ConversationNotFoundException();
		}
		return deserializeConversation(source);
	}

	public List<ElasticsearchConversation> getConversationsById(List<String> ids) {
		List<String> conversationIds = ids.stream().collect(Collectors.toList());
		if (conversationIds == null || conversationIds.isEmpty()) {
			return new ArrayList<>();
		}
		return Arrays.asList(buildGetUserConversationsRequest(conversationIds).execute().actionGet().getResponses())
					 .stream()
					 .map(MultiGetItemResponse::getResponse)
					 .map(Arrays::asList)
					 .flatMap(Collection::stream)
					 .map(GetResponse::getSourceAsString)
					 .map(this::deserializeConversation)
					 .collect(Collectors.toList());
	}

	public ElasticsearchConversation getConversationWithUsers(Set<Long> userIds) {
		List<List<String>> userConversationIds = userIds.stream()
														.map(this::getUserConversationIds)
														.collect(Collectors.toList());
		return Sets.intersection(Sets.newHashSet(userConversationIds.get(0)),
								 Sets.newHashSet(userConversationIds.get(1)))
				   .stream()
				   .map(this::getConversationById)
				   .filter(isSingle())
				   .filter(containsAllUsers(userIds))
				   .findAny()
				   .orElseThrow(ConversationNotFoundException::new);
	}

	public List<ElasticsearchConversation> getUserConversations(Long userId) {
		List<String> ids = getUserConversationIds(userId);
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}
		return Arrays.asList(buildGetUserConversationsRequest(ids).execute().actionGet().getResponses())
					 .stream()
					 .map(MultiGetItemResponse::getResponse)
					 .map(Arrays::asList)
					 .flatMap(Collection::stream)
					 .map(GetResponse::getSourceAsString)
					 .map(this::deserializeConversation)
					 .collect(Collectors.toList());
	}

	private Optional<ElasticSearchUser> getUser(Long userId) {
		GetResponse userDocument = client.prepareGet(indexName, "user", String.valueOf(userId))
										 .setRouting(userId.toString())
										 .get();
		return userDocument.getSourceAsString() != null ?
				Optional.of(deserializeUser(userDocument.getSourceAsString())) :
				Optional.empty();
	}

	public ElasticSearchMessage saveMessage(ElasticSearchMessage message) {
		client.prepareIndex(indexName, "conversation_data")
			  .setId(message.getId())
			  .setSource(serialize(message))
			  .setRouting(message.getConversationId())
			  .get();
		return message;
	}

	public void saveMessages(Map<String, List<ElasticSearchMessage>> elasticSearchMessageMap) {
		List<BulkRequestBuilder> bulkRequests = new ArrayList<>();
		for (String conversationId : elasticSearchMessageMap.keySet()) {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			List<ElasticSearchMessage> elasticSearchMessages = elasticSearchMessageMap.get(conversationId);
			for (ElasticSearchMessage elasticMessage : elasticSearchMessages) {
				bulkRequest.add(client.prepareIndex(indexName, "conversation_data")
									  .setRouting(elasticMessage.getConversationId())
									  .setId(elasticMessage.getId())
									  .setSource(serialize(elasticMessage)));

			}
			bulkRequests.add(bulkRequest);
		}
		bulkRequests.forEach(bulkRequestBuilder -> bulkRequestBuilder.execute().actionGet());
	}

	public void saveEvent(ElasticSearchEvent elasticSearchEvent) {
		client.prepareIndex(indexName, "conversation_data")
			  .setId(elasticSearchEvent.getConversationId() + "-" + elasticSearchEvent.getId())
			  .setSource(serialize(elasticSearchEvent))
			  .setRouting(elasticSearchEvent.getConversationId())
			  .get();
	}

	public List<ElasticsearchDataList> getConversationSyncData(Long userId, List<String> conversationIds, Date date,
			String application) {
		MultiSearchRequestBuilder multiSearchRequestBuilder = buildSyncRequest(userId, conversationIds, date.getTime(),
																			   application);

		if(multiSearchRequestBuilder == null) return new ArrayList<>();

		MultiSearchResponse multiSearchResponse = multiSearchRequestBuilder.get();
		List<ElasticsearchDataList> elasticsearchDataLists = new ArrayList<>();

		for (MultiSearchResponse.Item item : multiSearchResponse.getResponses()) {
			SearchResponse response = item.getResponse();
			SearchHits hits = response.getHits();
			buildGetMessagesBefore(hits, date.getTime(), userId, elasticsearchDataLists, application);
		}
		return elasticsearchDataLists;
	}

	public ElasticsearchDataList getConversationDataInRange(String conversationId, Range range, Long userId,
			String app) {
		SearchResponse response = buildHistoryRequest(conversationId, range, userId, app).get();
		SearchHits hits = response.getHits();
		return buildGetConversationDataInRange(conversationId, hits, userId);
	}

	public String createConversationDataId(String conversationId, Date date) {
		ElasticSearchDataId elasticSearchDataId = new ElasticSearchDataId(conversationId, date);
		return elasticSearchDataId.getHashedId();
	}

	//* ---------------------------------------------------------------------------------------------
	//  PRIVATE
	//* ---------------------------------------------------------------------------------------------

	private MultiSearchRequestBuilder buildSyncRequest(Long userId, List<String> conversationIds, Long date,
			String application) {

		Map<String, ElasticsearchConversation> conversations = getConversations(conversationIds);


		List<String> actualConversationIds = conversationIds.stream()
					   .filter(id -> conversations.get(id).getLastActivity().longValue() >= date.longValue())
					   .collect(Collectors.toList());

		if(actualConversationIds == null || actualConversationIds.size() == 0) return null;

		MultiSearchRequestBuilder multiSearchRequestBuilder = client.prepareMultiSearch();
		for (String conversationId : actualConversationIds) {
			TermQueryBuilder cQuery = QueryBuilders.termQuery("conversationId", conversationId);

			Long actualSyncDate = getActualSyncDate(userId, conversations.get(conversationId), date, application);
			BoolQueryBuilder eventBoolQuery = buildEventBoolQuery(userId, actualSyncDate, null, application);
			BoolQueryBuilder messageBoolQuery = buildMessageBoolQuery(userId, actualSyncDate, application);

			QueryBuilder conversationDataQuery = boolQuery().should(messageBoolQuery).should(eventBoolQuery);

			FilteredQueryBuilder Query = QueryBuilders.filteredQuery(cQuery, conversationDataQuery);

			multiSearchRequestBuilder.add(client.prepareSearch(indexName)
												.setSize(maxMessages)
												.setTypes("conversation_data")
												.addSort(SortBuilders.fieldSort("date").order(SortOrder.DESC))
												.setRouting(conversationId)
												.setQuery(Query));
		}
		return multiSearchRequestBuilder;

	}

	private Map<String, ElasticsearchConversation> getConversations(List<String> conversationIds) {
		Map<String, ElasticsearchConversation> response = new HashMap<>();
		MultiGetRequestBuilder builder = client.prepareMultiGet();

		conversationIds.stream().map(conversationId -> {
			return new MultiGetRequest.Item(indexName, "conversation", conversationId).routing(conversationId);
		}).forEach(builder::add);

		Arrays.asList(builder.execute().actionGet().getResponses())
			  .stream()
			  .map(MultiGetItemResponse::getResponse)
			  .map(Arrays::asList)
			  .flatMap(Collection::stream)
			  .map(GetResponse::getSourceAsString)
			  .map(s -> gson.fromJson(s, ElasticsearchConversation.class))
			  .forEach(d -> {
				  response.put(d.getId(), d);
			  });

		return response;
	}

	public Long getActualSyncDate(Long userId, ElasticsearchConversation d, Long date, String app) {
		Long deletedDate = d.getDeletedBy(userId, app) - 1;
		Long actualDate = date == null ? deletedDate : date;
		return Math.max(deletedDate, actualDate);
	}

	private Long getActualSyncDate(Long userId, Long date, String conversationId, String app) {
		GetResponse response = client.prepareGet(indexName, "conversation", conversationId)
									 .setRouting(conversationId)
									 .get();
		ElasticsearchConversationDeletion elasticsearchConversationDeletion = gson.fromJson(
				response.getSourceAsString(), ElasticsearchConversationDeletion.class);
		Long deletedDate = elasticsearchConversationDeletion.getDeletedBy(userId, app) - 1;
		Long actualDate = date == null ? deletedDate : date;
		return Math.max(deletedDate, actualDate);

	}

	private BoolQueryBuilder buildMessageBoolQuery(Long userId, Long date, String application) {
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("date").gt(date);
		List<String> types = Arrays.asList("audio", "image", "text", "video");
		RangeQueryBuilder receiptsRangeQuery = QueryBuilders.rangeQuery("receipts.date").gt(date);
		BoolQueryBuilder receiptsQuery = boolQuery().must(termQuery("sender", userId))
													.must(nestedQuery("receipts", receiptsRangeQuery));

		return boolQuery().must(termsQuery("type", types))
						  .must(termQuery("application", application))
						  .mustNot(termQuery("deletedBy", userId))
						  .mustNot(termQuery("ignoredBy", userId))
						  .should(rangeQuery)
						  .should(receiptsQuery);
	}

	private BoolQueryBuilder buildEventBoolQuery(Long userId, Long firstDate, Long lastDate, String application) {
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("date").gt(firstDate).lte(lastDate);

		BoolQueryBuilder isNotDeleteMessage = boolQuery().mustNot(termQuery("key", "DELETE_MESSAGE"));

		RangeQueryBuilder deleteMessageInRange = QueryBuilders.rangeQuery("eventsData.value").lt(firstDate);
		BoolQueryBuilder deleteMessageDate = boolQuery().must(termQuery("eventsData.key", "messageDate"))
														.must(deleteMessageInRange);
		BoolQueryBuilder deleteMessageQuery = boolQuery().must(termQuery("key", "DELETE_MESSAGE"))
														 .must(nestedQuery("eventsData", deleteMessageDate));

		return boolQuery().must(termQuery("type", "event"))
						  .must(termQuery("userId", userId))
						  .must(termQuery("application", application))
						  .must(rangeQuery)
						  .should(isNotDeleteMessage)
						  .should(deleteMessageQuery);

	}

	private SearchRequestBuilder buildHistoryRequest(String conversationId, Range range, Long userId, String app) {
		TermQueryBuilder idQuery = QueryBuilders.termQuery("conversationId", String.valueOf(conversationId));

		Long lastDate = range.getLastDate();
		Long actualFirstDate = getActualSyncDate(userId, range.getFirstDate(), conversationId, app);
		BoolQueryBuilder eventBoolQuery = buildEventBoolQuery(userId, actualFirstDate, lastDate, app);
		BoolQueryBuilder messageBoolQuery = buildHistoryMessageBoolQuery(userId, actualFirstDate, lastDate, app);

		QueryBuilder conversationDataQuery = boolQuery().should(eventBoolQuery).should(messageBoolQuery);
		FilteredQueryBuilder Query = QueryBuilders.filteredQuery(idQuery, conversationDataQuery);

		return client.prepareSearch(indexName)
					 .setSize(maxMessages)
					 .setTypes("conversation_data")
					 .setRouting(conversationId)
					 .addSort(SortBuilders.fieldSort("date").order(SortOrder.DESC).unmappedType("long"))
					 .setQuery(Query);
	}

	private BoolQueryBuilder buildHistoryMessageBoolQuery(Long userId, Long actualFirstDate, Long lastDate,
			String app) {
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("date").gt(actualFirstDate).lte(lastDate);
		List<String> types = Arrays.asList("audio", "image", "text", "video");
		RangeQueryBuilder receiptsRangeQuery = QueryBuilders.rangeQuery("receipts.date")
															.gt(actualFirstDate)
															.lte(lastDate);
		BoolQueryBuilder receiptsQuery = boolQuery().must(termQuery("sender", userId))
													.must(nestedQuery("receipts", receiptsRangeQuery));
		return boolQuery().must(termsQuery("type", types))
						  .must(termQuery("application", app))
						  .mustNot(termQuery("deletedBy", userId))
						  .mustNot(termQuery("ignoredBy", userId))
						  .should(rangeQuery)
						  .should(receiptsQuery);
	}

	public Long increaseCounter(String counter) {

		Script script = new Script("ctx._source.value += 1", ScriptService.ScriptType.INLINE, null, new HashMap<>());

		UpdateResponse updateResponse = client.prepareUpdate(indexName, "counters", counter)
											  .setScript(script)
											  .setUpsert("{\"value\":1}")
											  .setRetryOnConflict(99)
											  .setFields("value")
											  .get();

		return Long.valueOf((Integer) updateResponse.getGetResult().field("value").getValue());
	}

	private void buildGetMessagesBefore(SearchHits hits, Long syncDate, Long userId,
			List<ElasticsearchDataList> elasticsearchDataLists, String app) {
		List<ElasticSearchConversationData> conversationDataList = Observable.from(hits.getHits())
																			 .map(SearchHit::getSourceAsString)
																			 .map(this::deserializeData)
																			 .map(data -> processReceiptsInMessage
																					 (data,
																												   syncDate,
																												   userId))
																			 .toList()
																			 .toBlocking()
																			 .single();
		if (!conversationDataList.isEmpty()) {
			String conversationId = conversationDataList.get(0).getConversationId();
			elasticsearchDataLists.add(new ElasticsearchDataList(conversationDataList, conversationId,
																 Math.max(hits.getTotalHits() - maxMessages, 0)));
		}
	}

	private Long getUserUnreadMessages(String conversationId, Long userId, String app) {
		GetResponse response = client.prepareGet(indexName, "conversation", String.valueOf(conversationId)).get();
		ElasticsearchUnreadMessages elasticsearchUnreadMessages = gson.fromJson(response.getSourceAsString(),
																				ElasticsearchUnreadMessages.class);
		return elasticsearchUnreadMessages.getUnread(userId + "-" + app);
	}

	private ElasticSearchConversationData processReceiptsInMessage(
			ElasticSearchConversationData elasticSearchConversationData, Long date, Long userId) {
		if (isMessage(elasticSearchConversationData)) {
			ElasticSearchMessage elasticSearchMessage = (ElasticSearchMessage) elasticSearchConversationData;
			Long messageDate = elasticSearchConversationData.getDate();
			List<ElasticSearchIndividualMessageReceipt> elasticsearchIndividualReceipts = elasticSearchMessage
					.getReceipts();

			if (messageDate.compareTo(date) <= 0) {
				return buildMessageReceipt(date, userId, elasticSearchMessage, elasticsearchIndividualReceipts);
			} else {
				ElasticReceiptVisitor elasticReceiptVisitor = new ElasticReceiptVisitor(userId);
				return elasticSearchMessage.accept(elasticReceiptVisitor);
			}
		}
		return elasticSearchConversationData;
	}

	private ElasticsearchMessageReceipt buildMessageReceipt(Long date, Long userId,
			ElasticSearchMessage elasticSearchMessage,
			List<ElasticSearchIndividualMessageReceipt> elasticsearchIndividualReceipts) {
		List<ElasticSearchIndividualMessageReceipt> QueryedRceipts = elasticsearchIndividualReceipts.stream()
																									.filter(receipt ->
																													receipt.getDate()
																														   .compareTo(
																																   date)
																															> 0
																															&& elasticSearchMessage
																															.getSender()
																															.equals(userId))
																									.collect(
																											Collectors
																													.toList());
		String messageId = elasticSearchMessage.getId();
		String conversationId = elasticSearchMessage.getConversationId();
		String application = elasticSearchMessage.getApplication();
		return new ElasticsearchMessageReceipt(messageId, conversationId, QueryedRceipts, application);
	}

	private ElasticsearchDataList buildGetConversationDataInRange(String conversationId, SearchHits hits, Long
			userId) {
		List<ElasticSearchConversationData> conversationData = Stream.of(hits.getHits())
																	 .map(SearchHit::getSourceAsString)
																	 .map(this::deserializeData)
																	 .map(cd -> QueryUserReceipts(cd, userId))
																	 .collect(Collectors.toList());

		return new ElasticsearchDataList(conversationData, conversationId,
										 Math.max(hits.getTotalHits() - maxMessages, 0));

	}

	private ElasticSearchConversationData QueryUserReceipts(ElasticSearchConversationData cd, Long userId) {
		if (isMessage(cd)) {
			ElasticSearchMessage message = (ElasticSearchMessage) cd;
			ElasticReceiptVisitor elasticReceiptVisitor = new ElasticReceiptVisitor(userId);
			return message.accept(elasticReceiptVisitor);
		}
		return cd;
	}

	private ElasticSearchConversationData deserializeData(String source) {
		return gson.fromJson(source, ElasticSearchConversationData.class);
	}

	private ElasticsearchConversation deserializeConversation(String conversationString) {
		return gson.fromJson(conversationString, ElasticsearchConversation.class);
	}

	private String serialize(Object object) {
		return gson.toJson(object);
	}

	private Predicate<ElasticsearchConversation> containsAllUsers(Set<Long> userIds) {
		return conversation -> conversation.getUsers().containsAll(userIds);
	}

	private Predicate<ElasticsearchConversation> isSingle() {
		return conversation -> conversation.getUsers().size() == 2;
	}

	public void deleteMessage(String conversationId, String messageId, Long userId) {
		buildDeleteMessageRequest(conversationId, messageId, userId).get();
	}

	private UpdateRequestBuilder buildDeleteMessageRequest(String conversationId, String messageId, Long userId) {

		Script script = new Script(
				"if(ctx._source.containsKey(\"deletedBy\")){ ctx._source.deletedBy += tag; } else { " + "ctx"
						+ "._source.deletedBy = [tag] } ;", ScriptService.ScriptType.INLINE, null, new HashMap<>());
		script.getParams().put("tag", userId);

		return client.prepareUpdate(indexName, "conversation_data", messageId)
					 .setRouting(conversationId)
					 .setScript(script);
	}

	public void deleteConversation(String conversationId, Long userId, Long deletionDate, String app) {

		Script script = new Script("l=ctx._source.deletedBy;n=0;l.collect{it.key==userApp?it.value = date:n++};"
										   + "n==l.size()?l<<[key:userApp,value:date]:l;",
								   ScriptService.ScriptType.INLINE, null, new HashMap<>());
		script.getParams().put("userApp", userId.toString() + "-" + app);
		script.getParams().put("date", deletionDate);

		client.prepareUpdate(indexName, "conversation", conversationId)
			  .setRouting(conversationId)
			  .setScript(script)
			  .get();
	}

	//	public void deleteConversation(String conversationId, Long userId, Long deletionDate, String app) {
	//		ElasticSearchConversationDeletion deletion = new ElasticSearchConversationDeletion
	//				(app, conversationId, userId, deletionDate);
	//
	//		client.prepareIndex(indexName, "conversation_delete", getConversationDeletionID(userId,conversationId,app))
	//			  .setSource(serialize(deletion))
	//			  .setRouting(userId.toString())
	//			  .get();
	//	}

	public ElasticSearchIndividualMessageReceipt saveReceiptInMessage(
			ElasticSearchIndividualMessageReceipt elasticsearchIndividualReceipt, String conversationId,
			String messageId) {

		Script script = new Script(
				"receipt = [user : (user), type : type, date : (date)]; ctx._source.receipts += receipt;",
				ScriptService.ScriptType.INLINE, null, new HashMap<>());
		script.getParams().put("user", elasticsearchIndividualReceipt.getUserId());
		script.getParams().put("type", elasticsearchIndividualReceipt.getType());
		script.getParams().put("date", elasticsearchIndividualReceipt.getDate());

		client.prepareUpdate(indexName, "conversation_data", messageId)
			  .setScript(script)
			  .setRouting(conversationId)
			  .setRetryOnConflict(10)
			  .get();
		return elasticsearchIndividualReceipt;
	}

	public ElasticSearchMessage getConversationMessage(String conversationId, String id, Long userId) {
		GetResponse response = client.prepareGet(indexName, "conversation_data", id).setRouting(conversationId).get();
		ElasticSearchConversationData elasticSearchConversationData = deserializeData(response.getSourceAsString());
		if (elasticSearchConversationData == null || !isMessage(elasticSearchConversationData) || messageDeleted
				(userId,
																												 elasticSearchConversationData)) {
			throw new MessageNotFoundException();
		} else {
			elasticSearchConversationData.setId(id);
			return (ElasticSearchMessage) elasticSearchConversationData;
		}
	}

	private boolean messageDeleted(Long userId, ElasticSearchConversationData elasticSearchConversationData) {
		ElasticSearchMessage elasticSearchMessage = (ElasticSearchMessage) elasticSearchConversationData;
		return elasticSearchMessage.deletedBy().contains(userId);
	}

	private boolean isMessage(ElasticSearchConversationData elasticSearchConversationData) {
		String type = elasticSearchConversationData.getType();
		return type.equals("audio") || type.equals("image") || type.equals("video") || type.equals("text");
	}

	public List<ElasticSearchMessage> getAddressedMessages(String conversationId, Long userId, Date date, String app) {
		try {
			SearchRequestBuilder searchRequestBuilder = buildAddressedMessagesRequest(conversationId, userId,
																					  date.getTime(), app);
			SearchHits hits = searchRequestBuilder.get().getHits();
			return buildAddressedMessages(hits);
		} catch (ConversationNotFoundException e) {
			return new ArrayList<>();
		}
	}

	private List<ElasticSearchMessage> buildAddressedMessages(SearchHits hits) {
		return Observable.from(hits)
						 .map(SearchHit::getSourceAsString)
						 .map(this::deserializeMessage)
						 .toList()
						 .toBlocking()
						 .single();
	}

	private ElasticSearchMessage deserializeMessage(String source) {
		return gson.fromJson(source, ElasticSearchMessage.class);
	}

	private SearchRequestBuilder buildAddressedMessagesRequest(String conversationId, Long userId, Long date,
			String application) {
		Long actualSyncDate = getActualSyncDate(userId, date, conversationId, application);
		QueryBuilder isMessage = QueryBuilders.termsQuery("type", "text", "image", "audio", "video");
		QueryBuilder isInConversation = QueryBuilders.termQuery("conversationId", conversationId);
		QueryBuilder isInRange = QueryBuilders.rangeQuery("date").gt(actualSyncDate);
		TermQueryBuilder isFromApp = QueryBuilders.termQuery("application", application);
		QueryBuilder retrocompatibilityQuery = QueryBuilders.boolQuery()
															.must(isInConversation)
															.must(isFromApp)
															.must(isInRange)
															.must(isMessage)
															.mustNot(QueryBuilders.termQuery("deletedBy", userId))
															.mustNot(QueryBuilders.termQuery("ignoredBy", userId));
		return client.prepareSearch(indexName)
					 .setRouting(conversationId)
					 .setTypes("conversation_data")
					 .setQuery(retrocompatibilityQuery)
					 .addSort(SortBuilders.fieldSort("date").order(SortOrder.DESC))
					 .setSize(100);
	}

	private void updateUserDocument(String conversationId, Long userId) {
		buildUpdateUserDocumentRequest(Collections.singletonList(conversationId), userId).get();
	}

	public void clearRepository() {
		client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
		client.admin().indices().prepareCreate(indexName).execute().actionGet();
		client.admin()
			  .indices()
			  .preparePutMapping(indexName)
			  .setType("conversation_data")
			  .setSource(mapping())
			  .execute()
			  .actionGet();
		flush();
	}

	private String mapping() {
		return "{\n" + "     \"conversation_data\": {\n" + "       \"properties\": {\n"
				+ "         \"application\": {\n" + "           \"type\": \"string\",\n"
				+ "           \"index\":    \"not_analyzed\"\n" + "         },\n" + "         \"conversationId\": {\n"
				+ "           \"type\": \"string\",\n" + "\"index\":    \"not_analyzed\"\n         },\n"
				+ "         \"date\": {\n" + "           \"type\": \"long\"\n" + "         },\n"
				+ "         \"deletedBy\": {\n" + "           \"type\": \"long\"\n" + "         },\n"
				+ "         \"eventsData\": {\n" + "   \"type\": \"nested\",\n" + "           \"properties\": {\n"
				+ "             \"key\": {\n" + "               \"type\": \"string\",\n"
				+ "               \"index\":    \"not_analyzed\"\n" + "             },\n"
				+ "             \"value\": {\n" + "               \"type\": \"string\",\n"
				+ "               \"index\":    \"not_analyzed\"\n" + "             }\n" + "           }\n"
				+ "         },\n" + "         \"format\": {\n" + "           \"type\": \"string\",\n"
				+ "           \"index\":    \"not_analyzed\"\n" + "         },\n" + "         \"id\": {\n"
				+ "           \"type\": \"string\",\n" + "           \"index\":    \"not_analyzed\"\n" + "         "
				+ "},\n" + "         \"key\": {\n" + "           \"type\": \"string\",\n"
				+ "           \"index\":    \"not_analyzed\"\n" + "         },\n" + "         \"length\": {\n"
				+ "           \"type\": \"long\"\n" + "         },\n" + "         \"receipts\": {\n"
				+ "           \"type\": \"nested\",\n" + "           \"properties\": {\n" + "             \"date\": "
				+ "{\n" + "               \"type\": \"long\"\n" + "             },\n" + "             \"type\": {\n"
				+ "               \"type\": \"string\",\n" + "               \"index\":    \"not_analyzed\"\n"
				+ "             },\n" + "             \"user\": {\n" + "               \"type\": \"long\"\n"
				+ "             }\n" + "           }\n" + "         },\n" + "         \"sender\": {\n"
				+ "           \"type\": \"long\"\n" + "         },\n" + "         \"text\": {\n"
				+ "           \"type\": \"string\"\n" + "         },\n" + "         \"thumbnail\": {\n"
				+ "           \"type\": \"string\",\n" + "           \"index\":    \"not_analyzed\"\n" + "         "
				+ "},\n" + "         \"type\": {\n" + "           \"type\": \"string\",\n"
				+ "           \"index\":    \"not_analyzed\"\n" + "         },\n" + "         \"url\": {\n"
				+ "           \"type\": \"string\",\n" + "           \"index\":    \"not_analyzed\"\n" + "         "
				+ "},\n" + "         \"userId\": {\n" + "           \"type\": \"long\"\n" + "         }\n"
				+ "       }\n" + "     }\n" + "   }";
	}

	private ElasticSearchUser deserializeUser(String sourceAsString) {
		return sourceAsString != null ? gson.fromJson(sourceAsString, ElasticSearchUser.class) : null;
	}

	public void flush() {
		client.admin().indices().flush(new FlushRequest(indexName)).actionGet();
	}

	public Optional<ElasticSearchMessage> getLastMessage(String conversationId, String app) {
		QueryBuilder isMessage = QueryBuilders.termsQuery("type", "text", "image", "audio", "video");
		QueryBuilder isInConversation = QueryBuilders.termQuery("conversationId", conversationId);
		QueryBuilder isFromApplication = QueryBuilders.termQuery("application", app);
		QueryBuilder retrocompatibilityQuery = QueryBuilders.boolQuery()
															.must(isInConversation)
															.must(isMessage)
															.must(isFromApplication);
		SearchRequestBuilder searchResponse = client.prepareSearch(indexName)
													.setTypes("conversation_data")
													.setQuery(retrocompatibilityQuery)
													.addSort(SortBuilders.fieldSort("date").order(SortOrder.DESC))
													.setRouting(conversationId)
													.setSize(1);
		SearchHits hits = searchResponse.get().getHits();
		return hits.hits().length > 0 ? Optional.of(buildAddressedMessages(hits).get(0)) : Optional.empty();
	}

	public Map<Conversation, Optional<ElasticSearchMessage>> getLastMessages(List<Conversation> conversations,
			String app, Long userId) {

		if (conversations == null || conversations.isEmpty()) {
			return new HashMap<>();
		}

		Map<Conversation, Optional<ElasticSearchMessage>> mapResponse = new HashMap<>();

		MultiSearchRequestBuilder multiSearchRequestBuilder = buildMultiSearchRequest(app, userId, conversations);
		MultiSearchResponse multiSearchResponse = multiSearchRequestBuilder.get();

		for (MultiSearchResponse.Item item : multiSearchResponse.getResponses()) {
			SearchHits hits = item.getResponse().getHits();
			Optional<ElasticSearchMessage> elasticSearchOptional =
					hits.hits().length > 0 ? Optional.of(buildAddressedMessages(hits).get(0)) : Optional.empty();
			String conversationIdFromHit;
			if (elasticSearchOptional.isPresent()) {
				conversationIdFromHit = elasticSearchOptional.get().getConversationId();

				Conversation conversationFromHit = conversations.stream()
																.filter(conversation -> conversation.getId()
																									.equals
																											(conversationIdFromHit))
																.collect(Collectors.toList())
																.get(0);

				mapResponse.put(conversationFromHit, elasticSearchOptional);
			}
		}
		return mapResponse;
	}

	public List<ElasticsearchConversation> getActiveUserConversations(List<Conversation> conversations, Long userId,
			String app) {
		if (conversations.size() == 0) {
			return new ArrayList<>();
		}
		List<String> conversationIds = conversations.stream().map(Conversation::getId).collect(Collectors.toList());
		return getConversationsById(conversationIds).stream()
													.filter(conversation -> !conversation.hasDeleted(userId + "-" +
																											 app)
															|| conversationHasMessagesAfterLastDeletion(conversation,
																										userId, app))
													.collect(Collectors.toList());
	}

	private boolean conversationHasMessagesAfterLastDeletion(ElasticsearchConversation conversation, Long userId,
			String app) {
		Long lastDeletedDate = conversation.getDeletedBy(userId + "-" + app);
		return createMessagesAfterDateQuery(app, userId, conversation.getId(), lastDeletedDate);
	}

	private boolean createMessagesAfterDateQuery(String app, Long userId, String conversationId, Long date) {
		QueryBuilder isMessage = QueryBuilders.termsQuery("type", "text", "image", "audio", "video");
		QueryBuilder isInConversation = QueryBuilders.termQuery("conversationId", conversationId);
		QueryBuilder isFromApplication = QueryBuilders.termQuery("application", app);
		QueryBuilder isDeleted = QueryBuilders.termQuery("deletedBy", userId);
		QueryBuilder isAfterDate = QueryBuilders.rangeQuery("date").gt(date);
		QueryBuilder isIgnoredBy = QueryBuilders.termQuery("ignoredBy", userId);

		BoolQueryBuilder messagesAfterDateQuery = QueryBuilders.boolQuery()
															   .must(isInConversation)
															   .must(isMessage)
															   .mustNot(isIgnoredBy)
															   .must(isFromApplication)
															   .must(isAfterDate)
															   .mustNot(isDeleted);

		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName)
														  .setTypes("conversation_data")
														  .setQuery(messagesAfterDateQuery)
														  .addSort(SortBuilders.fieldSort("date").order(SortOrder
																												.DESC))
														  .setRouting(conversationId)
														  .setSize(1);

		SearchHits hits = searchRequestBuilder.get().getHits();
		return hits.totalHits() > 0;

	}

	private MultiSearchRequestBuilder buildMultiSearchRequest(String app, Long userId,
			List<Conversation> activeConversations) {
		List<String> conversationIds = activeConversations.stream()
														  .map(Conversation::getId)
														  .collect(Collectors.toList());
		MultiSearchRequestBuilder multiSearchRequestBuilder = client.prepareMultiSearch();

		for (String conversationId : conversationIds) {
			QueryBuilder retrocompatibilityQuery = createLastMessageQuery(app, userId, conversationId);

			multiSearchRequestBuilder.add(client.prepareSearch(indexName)
												.setTypes("conversation_data")
												.addSort(SortBuilders.fieldSort("date").order(SortOrder.DESC))
												.setRouting(conversationId)
												.setQuery(retrocompatibilityQuery)
												.setSize(1));
		}
		return multiSearchRequestBuilder;
	}

	private QueryBuilder createLastMessageQuery(String app, Long userId, String conversationId) {
		QueryBuilder isMessage = QueryBuilders.termsQuery("type", "text", "image", "audio", "video");
		QueryBuilder isInConversation = QueryBuilders.termQuery("conversationId", conversationId);
		QueryBuilder isFromApplication = QueryBuilders.termQuery("application", app);
		QueryBuilder isDeleted = QueryBuilders.termQuery("deletedBy", userId);
		QueryBuilder isIgnored = QueryBuilders.termQuery("ignoredBy", userId);
		return QueryBuilders.boolQuery()
							.must(isInConversation)
							.must(isMessage)
							.must(isFromApplication)
							.mustNot(isDeleted)
							.mustNot(isIgnored);
	}

	public boolean isAlreadyAcknowledged(String conversationId, String messageId,
			ElasticSearchIndividualMessageReceipt receipt) {
		GetResponse response = client.prepareGet(indexName, "conversation_data", messageId)
									 .setRouting(conversationId)
									 .get();
		ElasticSearchMessage elasticSearchMessage = (ElasticSearchMessage) deserializeData(
				response.getSourceAsString());
		return elasticSearchMessage.getReceipts()
								   .stream()
								   .anyMatch(r -> r.getUserId().equals(receipt.getUserId()) && r.getType()
																								.equals(receipt
																												.getType()));
	}

	private BulkRequestBuilder buildSaveConversationsRequest(List<ElasticsearchConversation> conversationsToSave) {
		BulkRequestBuilder bulk = client.prepareBulk();
		conversationsToSave.stream()
						   .map(conversation -> client.prepareIndex(indexName, "conversation")
													  .setId(String.valueOf(conversation.getId()))
													  .setRouting(conversation.getId())
													  .setSource(serialize(conversation)))
						   .forEach(bulk::add);
		return bulk;
	}

	private UpdateRequestBuilder buildUpdateUserDocumentRequest(List<String> conversationIds, Long userId) {
		try {

			if (conversationIds.size() >= 3000) {
				conversationIds = conversationIds.subList(0, 3000);
			}

			XContentBuilder source = jsonBuilder().startObject()
												  .array("conversations",
														 conversationIds.toArray(new String[conversationIds.size()]))
												  .field("count", new Long(conversationIds.size()))
												  .endObject();

			Script script = new Script("if(ctx._source.containsKey(\"conversations\")){"
											   + "if (!(ctx._source.conversations.size() >= 3000) && !(ctx._source"
											   + ".conversations.containsAll(conversationIds))) {"
											   + "  ctx._source.conversations.addAll(conversationIds - ctx._source"
											   + ".conversations);"
											   + "  ctx._source.count = ctx._source.conversations.size() " + "}"
											   + "} else { "
											   + "ctx._source.conversations = conversationIds; ctx._source.count = "
											   + "conversationIds.size() " + "} ;", ScriptService.ScriptType.INLINE,
									   null, new HashMap<>());
			script.getParams().put("conversationIds", conversationIds);

			return client.prepareUpdate(indexName, "user", userId.toString())
						 .setScript(script)
						 .setRetryOnConflict(10)
						 .setUpsert(source)
						 .setRouting(userId.toString());

		} catch (Exception e) {
			throw new ServerException(e, "Unknown error");
		}
	}

	MultiGetRequestBuilder buildGetUserConversationsRequest(List<String> conversationIds) {
		return conversationIds.stream()
							  .map(this::buildGetUserConversationRequestBulk)
							  .reduce(client.prepareMultiGet(), MultiGetRequestBuilder::add, (a, b) -> b);
	}

	private MultiGetRequest.Item buildGetUserConversationRequestBulk(String conversationId) {
		return new MultiGetRequest.Item(indexName, "conversation", conversationId).index(indexName)
																				  .type("conversation")
																				  .routing(conversationId);
	}

	private BulkRequestBuilder buildIndexConversationInUsersRequest(List<ElasticsearchConversation> response) {
		Map<Long, List<String>> userConversations = new HashMap<>();
		response.forEach(conversation -> {
			Set<Long> users = conversation.getUsers();
			users.forEach(user -> {
				List<String> userConv = userConversations.get(user);
				if (userConv == null) {
					userConv = new ArrayList<String>();
				}
				userConv.add(conversation.getId());
				userConversations.put(user, userConv);
			});
		});

		final BulkRequestBuilder bulk2 = client.prepareBulk();
		userConversations.keySet().stream().
				forEach(user -> bulk2.add(buildUpdateUserDocumentRequest(userConversations.get(user), user)));
		return bulk2;
	}

	private List<String> getIdsNotSaved(BulkResponse bulkResponse) {
		return Arrays.asList(bulkResponse.getItems())
					 .stream()
					 .filter(BulkItemResponse::isFailed)
					 .map(BulkItemResponse::getId)
					 .collect(Collectors.toList());
	}

	private List<String> getUserConversationIds(Long userId) {
		return getUser(userId).orElse(new ElasticSearchUser(userId, new ArrayList<>()))
							  .getConversations()
							  .stream()
							  .map(String::valueOf)
							  .collect(Collectors.toList());
	}

	private List<ElasticsearchConversation> QueryConversations(List<ElasticsearchConversation> conversationsToSave,
			List<String> idsNotSaved) {
		return conversationsToSave.stream().filter(c -> !idsNotSaved.contains(c.getId())).collect(Collectors.toList());
	}

	public RuntimeTypeAdapterFactory<ElasticSearchMessage> getAdapterFactoryForMessages() {
		return RuntimeTypeAdapterFactory.of(ElasticSearchMessage.class, "type")
										.registerSubtype(ElasticSearchTextMessage.class, "text")
										.registerSubtype(ElasticSearchImageMessage.class, "image")
										.registerSubtype(ElasticSearchAudioMessage.class, "audio")
										.registerSubtype(ElasticSearchVideoMessage.class, "video");
	}

	public RuntimeTypeAdapterFactory<ElasticSearchConversationData> getAdapterFactoryForConversations() {
		return RuntimeTypeAdapterFactory.of(ElasticSearchConversationData.class, "type")
										.registerSubtype(ElasticSearchTextMessage.class, "text")
										.registerSubtype(ElasticSearchImageMessage.class, "image")
										.registerSubtype(ElasticSearchAudioMessage.class, "audio")
										.registerSubtype(ElasticSearchVideoMessage.class, "video")
										.registerSubtype(ElasticSearchEvent.class, "event");
	}

	public String getMessageApplication(String conversationId, String messageId) {
		GetResponse response = client.prepareGet(indexName, "conversation_data", messageId)
									 .setRouting(conversationId)
									 .get();
		ElasticSearchConversationData elasticSearchConversationData = deserializeData(response.getSourceAsString());
		return elasticSearchConversationData.getApplication();
	}

	public Map<String, Long> getUnreadMessagesCount(List<String> ids, Long userId, String application) {
		List<String> conversationIds = ids.stream().collect(Collectors.toList());
		HashMap<String, Long> unreadMessageCountMap = new HashMap<>();
		if (conversationIds == null || conversationIds.isEmpty()) {
			return unreadMessageCountMap;
		}
		List<Long> collect = Arrays.asList(
				buildGetUserConversationsRequest(conversationIds).execute().actionGet().getResponses())
								   .stream()
								   .map(MultiGetItemResponse::getResponse)
								   .map(Arrays::asList)
								   .flatMap(Collection::stream)
								   .map(GetResponse::getSourceAsString)
								   .map(this::deserializeConversation)
								   .map(elasticsearchConversation -> elasticsearchConversation.getUnreadMessages(
										   userId + "-" + application))
								   .collect(Collectors.toList());
		for (String conversationId : ids) {
			unreadMessageCountMap.put(conversationId, collect.get(ids.indexOf(conversationId)));
		}
		return unreadMessageCountMap;
	}

	private UnreadMessageData deserializeUnreadMessageData(String conversationString) {
		return gson.fromJson(conversationString, UnreadMessageData.class);
	}

	public void updateLastActivity(String id) {
		Script script = new Script("ctx._source.lastActivity = new Date().getTime()");

		client.prepareUpdate(indexName, "conversation", id)
			  .setScript(script)
			  .setRouting(id)
			  .setRetryOnConflict(25)
			  .get();
	}
}
