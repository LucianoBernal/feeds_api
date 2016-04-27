package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Api(value = "/conversations", tags = "Conversations")
@Path("/conversations/query")
@Produces("application/json")
public class ConversationsQueryResource {

	private ConversationAdapter conversationAdapter;

	public ConversationsQueryResource(ConversationAdapter conversationAdapter) {
		this.conversationAdapter = conversationAdapter;
	}

	@GET
	@ApiOperation(value = "Queries the conversations", responseContainer = "List", response = ConversationDisplayDTO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error") })
	public List<ConversationDisplayDTO> getConversations(@QueryParam("user_id") Long userId) {
		return conversationAdapter.getUserConversations(userId);
	}

}
