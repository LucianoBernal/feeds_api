package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.ReceiptAdapter;
import com.etermax.conversations.dto.IndividualMessageReceiptCreationDTO;
import com.etermax.conversations.dto.IndividualMessageReceiptDisplayDTO;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Api(value = "/conversations", tags = "Receipts")
@Path("/conversations/{conversationId}/messages/{messageId}/receipts")
@Produces("application/json")
public class MessageReceiptsResource {

	private ReceiptAdapter receiptAdapter;

	public MessageReceiptsResource(ReceiptAdapter receiptAdapter) {
		this.receiptAdapter = receiptAdapter;
	}

	@POST
	@ApiOperation(value = "Saves receipts for a given message")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Client error"),
			@ApiResponse(code = 500, message = "Server error"), })
	public IndividualMessageReceiptDisplayDTO saveReceipt(
			@ApiParam(required = true) @PathParam("conversationId") String conversationId,
			@ApiParam(required = true) @PathParam("messageId") String messageId,
			@ApiParam(required = true) IndividualMessageReceiptCreationDTO individualMessageReceiptDTO) {
		return receiptAdapter.saveReceiptInMessage(conversationId, messageId, individualMessageReceiptDTO);
	}

}
