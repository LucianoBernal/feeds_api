package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Api(value = "/conversations", tags = "Conversations")
@Path("/conversations/{conversationId}")
@Produces("application/json")
public class ConversationResource {

	private ConversationAdapter conversationAdapter;

	public ConversationResource(ConversationAdapter conversationAdapter) {
		this.conversationAdapter = conversationAdapter;
	}

	@GET
	@ApiOperation(value = "Retrieves a conversation")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Not found"),
			@ApiResponse(code = 500, message = "Server error"), })
	public ConversationDisplayDTO getConversation(@PathParam("conversationId") String conversationId) {
		return conversationAdapter.getConversation(conversationId);
	}

}
