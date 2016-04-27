package com.etermax.conversations.factory;

import com.etermax.conversations.adapter.ReceiptAdapter;
import com.etermax.conversations.adapter.impl.ReceiptAdapterImpl;
import com.etermax.conversations.service.ReceiptService;

public class ReceiptAdapterFactory {
	private  ReceiptServiceFactory receiptServiceFactory;
	private IndividualMessageReceiptFactory individualMessageReceiptFactory;

	public ReceiptAdapterFactory(ReceiptServiceFactory receiptServiceFactory, IndividualMessageReceiptFactory individualMessageReceiptFactory) {
		this.receiptServiceFactory = receiptServiceFactory;
		this.individualMessageReceiptFactory = individualMessageReceiptFactory;
	}

	public ReceiptAdapter createReceiptAdapter(){
		ReceiptService receiptService = receiptServiceFactory.createReceiptService();
		return new ReceiptAdapterImpl(receiptService, individualMessageReceiptFactory);
	}
}
