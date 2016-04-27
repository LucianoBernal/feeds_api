package com.etermax.conversations.dto;

import com.etermax.conversations.model.HasMore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HasMoreDTO {

	@JsonProperty("has_more") private Boolean hasMore;

	@JsonProperty("first_date") private Long firstDate;

	@JsonProperty("last_date") private Long lastDate;

	@JsonProperty("total") private Integer totalMessages;

	@JsonProperty("date") private Long date;

	public HasMoreDTO(HasMore hasMore) {
		this.hasMore = hasMore.getHasMore();
		this.totalMessages = hasMore.getTotalMessages();
		if (this.hasMore) {
			this.date = hasMore.getLastDate().getTime() - 1;
			this.firstDate = hasMore.getFirstDate().getTime();
			this.lastDate = hasMore.getLastDate().getTime();
		}
	}

	public Long getFirstDate() {
		return firstDate;
	}

	public Boolean getHasMore() {
		return hasMore;
	}

	public Long getLastDate() {
		return lastDate;
	}

	public Integer getTotalMessages() {
		return totalMessages;
	}

	public Long getDate() {
		return date;
	}
}
