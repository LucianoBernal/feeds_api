package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.HistoryDTO;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Api(value = "/history", tags = "Syncronization")
@Path("/history")
@Produces("application/json")
public class HistoryResource {
	private SynchronizationAdapter synchronizationAdapter;

	public HistoryResource(SynchronizationAdapter synchronizationAdapter) {
		this.synchronizationAdapter = synchronizationAdapter;
	}

	@GET
	@ApiOperation(value = "Retrieves all user conversation data", responseContainer = "List", response = HistoryDTO.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Not found"),
			@ApiResponse(code = 500, message = "Server error"), })
	public HistoryDTO getMessagesFromId(
			@ApiParam(required = true) @QueryParam("seen_by") Long userId,
			@ApiParam(required = true) @QueryParam("conversation_id") String conversationId,
			@QueryParam("first_date") Long firstDate, @QueryParam("last_date") Long lastDate,
			@ApiParam(required = true) @QueryParam("application") String application) {
		return synchronizationAdapter.getConversationHistory(conversationId, firstDate, lastDate, userId, application);
	}
}
