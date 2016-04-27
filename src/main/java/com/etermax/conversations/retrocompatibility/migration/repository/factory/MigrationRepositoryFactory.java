package com.etermax.conversations.retrocompatibility.migration.repository.factory;

import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.etermax.conversations.retrocompatibility.migration.repository.factory.impl.MemoryMigrationRepositoryFactory;
import com.etermax.conversations.retrocompatibility.migration.repository.factory.impl.RedisMigrationRepositoryFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value=RedisMigrationRepositoryFactory.class, name="redis"),
		@JsonSubTypes.Type(value=MemoryMigrationRepositoryFactory.class, name="memory"),
		@JsonSubTypes.Type(value=OffMigrationRepositoryFactory.class, name="disabled")
})
public interface MigrationRepositoryFactory {

	MigrationRepository createRepository();

}
