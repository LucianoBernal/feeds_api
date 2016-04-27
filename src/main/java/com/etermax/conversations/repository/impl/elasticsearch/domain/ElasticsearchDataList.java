package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.List;

public class ElasticsearchDataList {
	private List<ElasticSearchConversationData> list;
	private String convId;
	private Long total;

	public ElasticsearchDataList(List<ElasticSearchConversationData> list, String convId, Long total) {
		this.list = list;
		this.convId = convId;
		this.total = total;
	}

	public List<ElasticSearchConversationData> getList() {
		return list;
	}

	public String getConversationId() {
		return convId;
	}

	public Long getTotal() {
		return total;
	}
}
