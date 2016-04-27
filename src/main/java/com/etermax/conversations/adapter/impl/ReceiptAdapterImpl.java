package com.etermax.conversations.adapter.impl;

import com.etermax.conversations.adapter.ReceiptAdapter;
import com.etermax.conversations.dto.IndividualMessageReceiptCreationDTO;
import com.etermax.conversations.dto.IndividualMessageReceiptDisplayDTO;
import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.IndividualMessageReceiptFactory;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.service.ReceiptService;

public class ReceiptAdapterImpl implements ReceiptAdapter {

	private ReceiptService receiptService;
	private IndividualMessageReceiptFactory individualMessageReceiptFactory;

	public ReceiptAdapterImpl(ReceiptService receiptService, IndividualMessageReceiptFactory individualMessageReceiptFactory) {
		this.receiptService = receiptService;
		this.individualMessageReceiptFactory = individualMessageReceiptFactory;
	}

	@Override
	public IndividualMessageReceiptDisplayDTO saveReceiptInMessage(String conversationId, String messageId,
			IndividualMessageReceiptCreationDTO dto) {
		IndividualMessageReceipt savedReceipt;
		try {
			dto.validate();
			savedReceipt = receiptService.saveReceiptInMessage(conversationId, messageId, createIndividualMessageReceipt(
					dto));
		} catch (SaveReceiptException | InvalidDTOException e) {
			throw new ClientException(e, 400);
		} catch (AlreadyAcknowledgedMessageException e){
			throw new ClientException(e, 422);
		}
		return new IndividualMessageReceiptDisplayDTO(savedReceipt);
	}

	private IndividualMessageReceipt createIndividualMessageReceipt(IndividualMessageReceiptCreationDTO dto) {
		try {
		return individualMessageReceiptFactory.createIndividualMessageReceipt(dto.getType(), dto.getUserId());
		} catch (ModelException e) {
			throw new ServerException(e, "");
		}
	}

}
