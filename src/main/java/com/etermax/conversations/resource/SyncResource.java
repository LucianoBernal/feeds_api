package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.dto.SyncDTO;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Api(value = "/sync", tags = "Syncronization")
@Path("/sync")
@Produces("application/json")
public class SyncResource {
	private SynchronizationAdapter syncAdapter;

	public SyncResource(SynchronizationAdapter syncAdapter) {
		this.syncAdapter = syncAdapter;
	}

	@GET
	@ApiOperation(value = "Syncronizes all the user info", responseContainer = "List", response = SyncDTO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"), })
	public List<SyncDTO> getConversationSync(@ApiParam(required = true) @QueryParam("userId") Long userId,
			@QueryParam("last_sync") String dateString,
			@ApiParam(required = true) @QueryParam("application") String application) {
		return syncAdapter.getConversationSync(userId, dateString, application);
	}

}

