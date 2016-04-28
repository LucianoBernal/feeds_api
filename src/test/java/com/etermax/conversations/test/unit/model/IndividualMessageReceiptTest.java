package com.etermax.conversations.test.unit.model;

import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.model.ReceivedType;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IndividualMessageReceiptTest {

	@Test
	public void individualMessageReceiptTest() throws ModelException {
		//Given

		//When
		IndividualMessageReceipt individualMessageReceipt = new IndividualMessageReceipt(new ReceivedType(), 1l);

		//Then
		assertThat(individualMessageReceipt.getType()).isInstanceOf(ReceivedType.class);
		assertThat(individualMessageReceipt.getUser()).isEqualTo(1l);
	}

	@Test
	public void invalidTypeTest() {
		//Given When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> new IndividualMessageReceipt(null, 1l);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ModelException.class);
	}

	@Test
	public void invalidDateTest() {
		//Given When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> new IndividualMessageReceipt(new ReceivedType(), null);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ModelException.class);
	}

	@Test
	public void invalidUserTest() {
		//Given When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> new IndividualMessageReceipt(new ReceivedType(),
				null);

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ModelException.class);
	}
}
