package com.etermax.conversations.test.unit.resource;

import com.etermax.conversations.dto.AppInfoDTO;
import com.etermax.conversations.resource.ApplicationResource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationResourceTest {

	@Test
	public void testGetApplicationInfo() throws Exception {
		//GIVEN
		ApplicationResource applicationResource = new ApplicationResource();

		//WHEN
		AppInfoDTO appInfoDTO = applicationResource.getAppInfo();

		//THEN
		assertThat(appInfoDTO).isNotNull();
	}

}
