package com.etermax.conversations.retrocompatibility.resource;

import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityConversationAdapter;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityChatHeadersDTO;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Api(value = "/retrocompatibility", tags = "Retrocompatibility")
@Path("/retrocompatibility/chat_headers")
@Produces("application/json" + "; charset=utf-8")
public class RetrocompatibilityChatHeadersResource {

	private RetrocompatibilityConversationAdapter adapter;

	public RetrocompatibilityChatHeadersResource(RetrocompatibilityConversationAdapter adapter) {
		this.adapter = adapter;
	}

	@GET
	public RetrocompatibilityChatHeadersDTO getChatHeaders(@QueryParam("user") Long userId,
			@QueryParam("application") String application) {
		return adapter.getChatHeaders(userId, application);
	}

}
