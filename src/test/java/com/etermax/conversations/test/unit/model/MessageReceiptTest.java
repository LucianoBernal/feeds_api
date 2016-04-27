package com.etermax.conversations.test.unit.model;

import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.model.MessageReceipt;
import com.etermax.conversations.model.ReceivedType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageReceiptTest {

	@Test
	public void messageReceiptTest() throws ModelException {
		//Given
		List<IndividualMessageReceipt> individualMessageReceiptList = new ArrayList<>();
		individualMessageReceiptList.add(new IndividualMessageReceipt(new ReceivedType(), 1l));

		//When
		MessageReceipt messageReceipt = new MessageReceipt("1", individualMessageReceiptList, "1", "A2");

		//Then
		assertThat(messageReceipt.getReceipts().get(0).getType()).isInstanceOf(ReceivedType.class);
		assertThat(messageReceipt.getReceipts()).extracting("userId").containsExactly(1l);
	}

}
