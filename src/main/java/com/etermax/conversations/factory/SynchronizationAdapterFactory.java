package com.etermax.conversations.factory;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.adapter.impl.SynchronizationAdapterImpl;
import com.etermax.conversations.service.SynchronizationService;

public class SynchronizationAdapterFactory {
	private SynchronizationServiceFactory synchronizationServiceFactory;

	public SynchronizationAdapterFactory(SynchronizationServiceFactory synchronizationServiceFactory) {
		this.synchronizationServiceFactory = synchronizationServiceFactory;
	}

	public SynchronizationAdapter createSyncronizationAdapter(){
		SynchronizationService synchronizationService = synchronizationServiceFactory.createSynchronizationService();
		return new SynchronizationAdapterImpl(synchronizationService);
	}
}
