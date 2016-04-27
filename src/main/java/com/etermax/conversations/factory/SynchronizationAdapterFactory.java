package com.etermax.conversations.factory;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.adapter.impl.SynchronizationAdapterImpl;
import com.etermax.conversations.retrocompatibility.migration.service.MigrationService;
import com.etermax.conversations.service.SynchronizationService;

public class SynchronizationAdapterFactory {
	private SynchronizationServiceFactory synchronizationServiceFactory;
	private MigrationService migrationService;

	public SynchronizationAdapterFactory(SynchronizationServiceFactory synchronizationServiceFactory,
			MigrationService migrationService) {
		this.synchronizationServiceFactory = synchronizationServiceFactory;
		this.migrationService = migrationService;
	}

	public SynchronizationAdapter createSyncronizationAdapter(){
		SynchronizationService synchronizationService = synchronizationServiceFactory.createSynchronizationService();
		return new SynchronizationAdapterImpl(synchronizationService, migrationService);
	}
}
