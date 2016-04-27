package com.etermax.conversations.retrocompatibility.migration.repository.factory.impl;

import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.etermax.conversations.retrocompatibility.migration.repository.factory.MigrationRepositoryFactory;
import com.etermax.conversations.retrocompatibility.migration.repository.impl.MemoryMigrationRepository;

public class MemoryMigrationRepositoryFactory implements MigrationRepositoryFactory{

	@Override
	public MigrationRepository createRepository() {
		return new MemoryMigrationRepository();
	}
}
