package com.etermax.conversations.retrocompatibility.resource;

import com.etermax.conversations.dto.AddressedMessageCreationDTO;
import com.etermax.conversations.metrics.MetricContainer;
import com.etermax.conversations.metrics.MetricsPublisher;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityConversationAdapter;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityConversationDTO;
import com.etermax.conversations.retrocompatibility.dto.RetrocompatibilityMessageDTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Api(value = "/retrocompatibility", tags = "Retrocompatibility")
@Path("/retrocompatibility/messages")
@Produces("application/json" + "; charset=utf-8")
public class RetrocompatibilityMessagesResource {

	private RetrocompatibilityConversationAdapter adapter;
	private MetricsPublisher metricsPublisher;

	public RetrocompatibilityMessagesResource(RetrocompatibilityConversationAdapter adapter, MetricsPublisher metricsPublisher) {
		this.adapter = adapter;
		this.metricsPublisher = metricsPublisher;
	}

	@GET
	@ApiOperation(value = "Gets the messages between two users")
	public RetrocompatibilityConversationDTO getMessages(@ApiParam(required = true) @QueryParam("users") String
			users, @ApiParam(required = true) @QueryParam("application") String application) {
		return adapter.getMessages(getUserIds(users), application);
	}

	@POST
	@ApiOperation(value = "Creates a new message between two users")
	public RetrocompatibilityMessageDTO saveMessage(@ApiParam(required = true) AddressedMessageCreationDTO message) {
		if (this.metricsPublisher != null) {
			publishRetrocompatibilityMetrics(message);
		}
		return adapter.saveMessage(message);
	}

	private List<Long> getUserIds(String users) {
		return Stream.of(users.split(",")).map(Long::valueOf).collect(Collectors.toList());
	}

	private void publishRetrocompatibilityMetrics(@ApiParam(required = true) AddressedMessageCreationDTO message) {
		MetricContainer metricContainer = createMetricContainer(message);
		metricsPublisher.publishAll(metricContainer);
	}

	private MetricContainer createMetricContainer(AddressedMessageCreationDTO message) {
		String application = message.getApplication();
		String messageType = "text";
		return new MetricContainer(application, messageType);
	}

}
