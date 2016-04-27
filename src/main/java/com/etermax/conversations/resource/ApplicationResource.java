package com.etermax.conversations.resource;

import com.etermax.conversations.dto.AppInfoDTO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
@Produces("application/json")
public class ApplicationResource {

	@GET
	public AppInfoDTO getAppInfo() {
		return AppInfoDTO.getInstance();
	}
}
