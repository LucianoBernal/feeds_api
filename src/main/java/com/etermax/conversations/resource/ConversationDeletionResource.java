package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.ConversationAdapter;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.dto.DeleteConversationDisplayDTO;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Api(value = "/conversations", tags = "Conversations")
@Path("/conversations/{conversationId}/delete")
@Produces("application/json")
public class ConversationDeletionResource {
	private ConversationAdapter conversationAdapter;

	public ConversationDeletionResource(ConversationAdapter adapter) {
		this.conversationAdapter = adapter;
	}

	@POST
	@ApiOperation(value = "Marks all the messages of the conversation as erased by a given user")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Client error"),
			@ApiResponse(code = 500, message = "Server error"), })
	public DeleteConversationDisplayDTO deleteConversation(@PathParam("conversationId") String conversationId,
			@ApiParam(required = true) ConversationMessageDeletionDTO deletionDTO) {
		return conversationAdapter.deleteConversation(conversationId, deletionDTO);
	}
}
