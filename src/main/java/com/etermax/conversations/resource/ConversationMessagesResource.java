package com.etermax.conversations.resource;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.BaseMessageCreationDTO;
import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.metrics.GraphiteNotificationMetricPublisher;
import com.etermax.conversations.metrics.MetricContainer;
import com.etermax.conversations.metrics.MetricsPublisher;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Api(value = "/conversations", tags = "Conversations")
@Path("/conversations/{conversationId}/messages")
@Produces("application/json")
public class ConversationMessagesResource {

	private MessageAdapter messageAdapter;
	private MetricsPublisher notificationMetricPublisher;

	public ConversationMessagesResource(MessageAdapter messageAdapter) {
		this.messageAdapter = messageAdapter;
		this.notificationMetricPublisher = null;
	}

	public ConversationMessagesResource(MessageAdapter messageAdapter,
			MetricsPublisher metricsPublisher) {
		this.messageAdapter = messageAdapter;
		this.notificationMetricPublisher = metricsPublisher;
	}

	@POST
	@ApiOperation(value = "Creates a message in a conversation")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Client error"),
			@ApiResponse(code = 500, message = "Server error"), })
	public ConversationDataDTO saveMessage(@PathParam("conversationId") String conversationId,
			@ApiParam(required = true) BaseMessageCreationDTO message) {
		if (notificationMetricPublisher == null) {
			return message.accept(messageAdapter, conversationId);
		} else {
			notificationMetricPublisher.publishAll(new MetricContainer(message));
			return message.accept(messageAdapter, conversationId);
		}
	}

}
