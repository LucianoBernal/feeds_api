package com.etermax.conversations.retrocompatibility.resource;

import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityConversationAdapter;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityConversationDeletionDTO;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/retrocompatibility/conversation_delete")
@Produces("application/json")
public class RetrocompatibilityConversationDeletionResource {

	public RetrocompatibilityConversationDeletionResource(
			RetrocompatibilityConversationAdapter retrocompatibilityAdapter) {
		this.retrocompatibilityAdapter = retrocompatibilityAdapter;
	}

	private RetrocompatibilityConversationAdapter retrocompatibilityAdapter;

	@POST
	@ApiOperation(value = "Marks all the messages of the conversation as erased by a given user")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Client error"),
			@ApiResponse(code = 500, message = "Server error"), })
	public void deleteConversation(RetrocompatibilityConversationDeletionDTO deletionDTO) {
		retrocompatibilityAdapter.deleteConversation(deletionDTO);
	}
}
