package com.etermax.conversations.test.unit.dto;

import com.etermax.conversations.dto.BaseMessageCreationDTO;
import com.etermax.conversations.model.MessageVisitor;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class BaseMessageCreationDTOTest {

	@Test
	public void testAccesorsBaseMessageCreationDTO() {
		//GIVEN
		BaseMessageCreationDTO baseMessageCreationDTO = new BaseMessageCreationDTO();

		//WHEN
		baseMessageCreationDTO.setSenderId(1l);

		//THEN
		assertThat(baseMessageCreationDTO.getSenderId()).isEqualTo(1l);
	}

	@Test
	public void testAcceptBaseMessageCreationDTO() {
		//GIVEN
		MessageVisitor messageVisitor = mock(MessageVisitor.class);
		BaseMessageCreationDTO baseMessageCreationDTO = new BaseMessageCreationDTO();

		//WHEN
		baseMessageCreationDTO.setSenderId(1l);
		ThrowableAssert.ThrowingCallable throwingCallable = () -> baseMessageCreationDTO.accept(messageVisitor, "1");

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(RuntimeException.class).hasMessage("Wrong Message Creation Instantiation");
	}
}