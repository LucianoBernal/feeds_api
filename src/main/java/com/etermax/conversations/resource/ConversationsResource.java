package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationCreationDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api(value = "/conversations", tags = "Conversations")
@Path("/conversations")
@Produces("application/json")
public class ConversationsResource {

	private ConversationAdapter conversationAdapter;

	public ConversationsResource(ConversationAdapter conversationAdapter) {
		this.conversationAdapter = conversationAdapter;
	}

	@POST
	@ApiOperation(value = "Creates a conversation")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Client error"),
			@ApiResponse(code = 500, message = "Server error"), })
	public ConversationDisplayDTO saveConversation(
			@ApiParam(required = true) ConversationCreationDTO conversationCreationDTO) {
		return conversationAdapter.saveConversation(conversationCreationDTO);
	}

}
