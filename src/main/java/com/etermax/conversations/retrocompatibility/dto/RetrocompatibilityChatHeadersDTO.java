package com.etermax.conversations.retrocompatibility.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RetrocompatibilityChatHeadersDTO {

	@JsonProperty("chat_headers")
	private List<List<RetrocompatibilityChatHeaderDTO>> chatHeaders;

	@JsonProperty("has_more")
	private Boolean hasMore;

	public RetrocompatibilityChatHeadersDTO(List<RetrocompatibilityChatHeaderDTO> chatHeaders) {
		Collections.sort(chatHeaders, (o1, o2) -> o2.getLastActivityDate().compareTo(o1.getLastActivityDate()));
		ArrayList<List<RetrocompatibilityChatHeaderDTO>> headers = new ArrayList<>();
		headers.add(chatHeaders);
		this.chatHeaders = headers;
		this.hasMore = Boolean.FALSE;
	}

	public List<List<RetrocompatibilityChatHeaderDTO>> getChatHeaders() {
		return chatHeaders;
	}

	public void setChatHeaders(List<List<RetrocompatibilityChatHeaderDTO>> chatHeaders) {
		this.chatHeaders = chatHeaders;
	}

	public Boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(Boolean hasMore) {
		this.hasMore = hasMore;
	}
}
