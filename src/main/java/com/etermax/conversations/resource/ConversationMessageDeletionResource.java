package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Api(value = "/conversations", tags = "Conversations")
@Path("/conversations/{conversationId}/messages/{messageId}/delete")
@Produces("application/json")
public class ConversationMessageDeletionResource {

	private MessageAdapter messageAdapter;

	public ConversationMessageDeletionResource(MessageAdapter adapter) {
		this.messageAdapter = adapter;
	}

	@POST
	@ApiOperation(value = "Marks a message as erased by a given user")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Client error"),
			@ApiResponse(code = 500, message = "Server error"), })
	public void deleteMessage(@PathParam("conversationId") String conversationId, @PathParam("messageId") String messageId,
			@ApiParam(required = true) ConversationMessageDeletionDTO deletionDTO) {
		messageAdapter.deleteMessage(conversationId, messageId, deletionDTO);
	}

}
