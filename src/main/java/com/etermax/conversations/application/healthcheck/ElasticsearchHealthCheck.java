package com.etermax.conversations.application.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;

public class ElasticsearchHealthCheck extends HealthCheck {

	private Client client;

	public ElasticsearchHealthCheck(Client client) {
		this.client = client;
	}

	@Override
	protected Result check() throws Exception {
		ClusterHealthStatus status = client.admin().cluster().prepareHealth().get().getStatus();
		if (status != ClusterHealthStatus.GREEN){
			Result.unhealthy("Cluster is in " + status + " state");
		}
		return Result.healthy();
	}
}
