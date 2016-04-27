package com.etermax.conversations.retrocompatibility.resource;

import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityMessageAdapter;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityConversationMessageDeletionDTO;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/retrocompatibility/message_delete")
@Produces("application/json")
public class RetrocompatibilityMessageDeletionResource {
	private RetrocompatibilityMessageAdapter retrocompatibilityMessageAdapter;

	public RetrocompatibilityMessageDeletionResource(
			RetrocompatibilityMessageAdapter retrocompatibilityMessageAdapter) {
		this.retrocompatibilityMessageAdapter = retrocompatibilityMessageAdapter;
	}

	@POST
	@ApiOperation(value = "Marks a message as erased by a given user")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Client error"),
			@ApiResponse(code = 500, message = "Server error"), })
	public void deleteMessage(RetrocompatibilityConversationMessageDeletionDTO deletionDTO) {
		retrocompatibilityMessageAdapter.deleteMessage(deletionDTO);
	}
}
