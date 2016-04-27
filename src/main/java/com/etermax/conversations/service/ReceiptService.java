package com.etermax.conversations.service;

import com.etermax.conversations.error.SaveReceiptException;
import com.etermax.conversations.model.IndividualMessageReceipt;

public interface ReceiptService {
	IndividualMessageReceipt saveReceiptInMessage(String conversationId, String messageId, IndividualMessageReceipt receipt)
			throws SaveReceiptException;
}
