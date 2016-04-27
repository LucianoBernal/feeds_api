package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.AppInfoDTO;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppInfoDTOTest {

	@Test
	public void testAppInfoDTO() throws Exception {
		//GIVEN
		AppInfoDTO dto = AppInfoDTO.getInstance();

		//WHEN
		String version = dto.getVersion();

		//THEN
		assertThat(version).isNotNull();
	}

}
