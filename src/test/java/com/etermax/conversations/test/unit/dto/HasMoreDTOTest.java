package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.HasMoreDTO;
import com.etermax.conversations.model.HasMore;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class HasMoreDTOTest {

	@Test
	public void hasMoreMessageDTOTest()  {
		//Given
		HasMore hasMoreMessage = new HasMore(1, new Date(3), new Date(4));

		//When
		HasMoreDTO hasMoreDTO = new HasMoreDTO(hasMoreMessage);

		//Then
		assertThat(hasMoreDTO.getTotalMessages()).isEqualTo(hasMoreMessage.getTotalMessages());
		assertThat(hasMoreDTO.getFirstDate()).isEqualTo(hasMoreMessage.getFirstDate().getTime());
		assertThat(hasMoreDTO.getLastDate()).isEqualTo(hasMoreMessage.getLastDate().getTime());
		assertThat(hasMoreDTO.getHasMore()).isEqualTo(hasMoreMessage.getHasMore());
		assertThat(hasMoreDTO.getDate()).isEqualTo(hasMoreMessage.getLastDate().getTime() - 1);
	}
}
