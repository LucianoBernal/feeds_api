package com.etermax.conversations.factory;

import com.etermax.conversations.error.ModelException;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.model.ReadType;
import com.etermax.conversations.model.ReceiptType;
import com.etermax.conversations.model.ReceivedType;

public class IndividualMessageReceiptFactory {
	public IndividualMessageReceipt createIndividualMessageReceipt(String stringType, Long userId)
			throws ModelException {
		ReceiptType type;
		if(stringType.equals("read")) {
			type = new ReadType();
		}else{
			type = new ReceivedType();
		}
		return new IndividualMessageReceipt(type, userId);
	}
}
