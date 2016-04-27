package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.List;
import java.util.stream.Collectors;

public class ElasticsearchConversationDeletion {
	private List<DeletionData> deletedBy;

	public Long getDeletedBy(Long userId, String app) {
		List<DeletionData> del = deletedBy.stream().filter(deletionData ->
				deletionData.getKey().equals(userId +"-"+ app)).collect(Collectors.toList());
		return del.isEmpty() ? 0L : del.get(0).getValue();
	}
}
