package com.etermax.conversations.adapter;

import com.etermax.conversations.dto.IndividualMessageReceiptCreationDTO;
import com.etermax.conversations.dto.IndividualMessageReceiptDisplayDTO;

public interface ReceiptAdapter {
	IndividualMessageReceiptDisplayDTO saveReceiptInMessage(String conversationId, String messageId, IndividualMessageReceiptCreationDTO dto);
}

