package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.dto.AddressedMessageDisplayDTO;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api(value = "/messages", tags = "Messages")
@Path("/messages")
@Produces("application/json")
public class MessagesResource {
	MessageAdapter messageAdapter;

	public MessagesResource(MessageAdapter messageAdapter) {
		this.messageAdapter = messageAdapter;
	}

	@POST
	@ApiOperation(value = "Creates a new message between two users")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Client error"),
			@ApiResponse(code = 500, message = "Server error"), })
	public AddressedMessageDisplayDTO saveMessage(@ApiParam(required = true) AddressedMessageCreationDTO message) {
		return messageAdapter.saveMessage(message);
	}
}
