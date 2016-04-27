package com.etermax.conversations.test.unit.migration.service;

import com.etermax.conversations.factory.AddressedMessageFactory;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.ConversationTextMessage;
import com.etermax.conversations.model.User;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.repository.impl.memory.MemoryConversationRepository;
import com.etermax.conversations.retrocompatibility.migration.domain.MigrationResult;
import com.etermax.conversations.retrocompatibility.migration.repository.MigrationRepository;
import com.etermax.conversations.retrocompatibility.migration.service.MigrationService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationServiceTest {

	@Test
	public void testMigrateConversation() {
		ConversationRepository newRepo = new MemoryConversationRepository(new AddressedMessageFactory(), 99, 99);
		MigrationRepository oldRepo = new TestMigrationRepository();
		MigrationService migrationService = new MigrationService(oldRepo, newRepo);

		List<MigrationResult> migrationResults = migrationService.migrateConversations(1L);

		assertThat(migrationResults.size()).isEqualTo(1);
		assertThat(migrationResults.get(0).getMessagesMigratedCount()).isEqualTo(1L);
	}

	@Test
	public void testMigrateConversationToMultipleApps() {
		ConversationRepository newRepo = new MemoryConversationRepository(new AddressedMessageFactory(), 99, 99);
		MigrationRepository oldRepo = new TestMigrationRepository();
		MigrationService migrationService = new MigrationService(oldRepo, newRepo);

		List<MigrationResult> migrationResults = migrationService.migrateConversations(3L);

		assertThat(migrationResults.size()).isEqualTo(1);
		assertThat(migrationResults.get(0).getMessagesMigratedCount()).isEqualTo(2L);
	}

	@Test
	public void testMigrateConversationWithNoAppsInCommon() {
		ConversationRepository newRepo = new MemoryConversationRepository(new AddressedMessageFactory(), 99, 99);
		MigrationRepository oldRepo = new TestMigrationRepository();
		MigrationService migrationService = new MigrationService(oldRepo, newRepo);

		List<MigrationResult> migrationResults = migrationService.migrateConversations(5L);

		assertThat(migrationResults.size()).isEqualTo(0);
	}

	@Test
	public void testMigrateConversationWithOnlyOneAppInCommon() {
		ConversationRepository newRepo = new MemoryConversationRepository(new AddressedMessageFactory(), 99, 99);
		MigrationRepository oldRepo = new TestMigrationRepository();
		MigrationService migrationService = new MigrationService(oldRepo, newRepo);

		List<MigrationResult> migrationResults = migrationService.migrateConversations(7L);

		assertThat(migrationResults.size()).isEqualTo(1);
		assertThat(migrationResults.get(0).getMessagesMigratedCount()).isEqualTo(1L);
	}

	@Test
	public void testMigrateConversationWithMultipleMessages() {
		ConversationRepository newRepo = new MemoryConversationRepository(new AddressedMessageFactory(), 99, 99);
		MigrationRepository oldRepo = new TestMigrationRepository();
		MigrationService migrationService = new MigrationService(oldRepo, newRepo);

		List<MigrationResult> migrationResults = migrationService.migrateConversations(9L);

		assertThat(migrationResults.size()).isEqualTo(1);
		assertThat(migrationResults.get(0).getMessagesMigratedCount()).isEqualTo(3L);
	}

	@Test
	public void testMigrateConversationWithMultipleMessagesAndApps() {
		ConversationRepository newRepo = new MemoryConversationRepository(new AddressedMessageFactory(), 99, 99);
		MigrationRepository oldRepo = new TestMigrationRepository();
		MigrationService migrationService = new MigrationService(oldRepo, newRepo);

		List<MigrationResult> migrationResults = migrationService.migrateConversations(11L);

		assertThat(migrationResults.size()).isEqualTo(1);
		assertThat(migrationResults.get(0).getMessagesMigratedCount()).isEqualTo(6L);
	}

	@Test
	public void testMigrateMultipleConversationsWithMultipleMessagesAndApps() {
		ConversationRepository newRepo = new MemoryConversationRepository(new AddressedMessageFactory(), 99, 99);
		MigrationRepository oldRepo = new TestMigrationRepository();
		MigrationService migrationService = new MigrationService(oldRepo, newRepo);

		List<MigrationResult> migrationResults = migrationService.migrateConversations(13L);

		assertThat(migrationResults.size()).isEqualTo(2);
		assertThat(migrationResults.get(0).getMessagesMigratedCount()).isEqualTo(6L);
		assertThat(migrationResults.get(1).getMessagesMigratedCount()).isEqualTo(6L);
	}

	private class TestMigrationRepository implements MigrationRepository {

		@Override
		public List<Conversation> getConversations(Long userId) {
			if (userId.equals(1L)) {
				return Arrays.asList(new Conversation(Sets.newHashSet(new User(1L), new User(2L)), new Date()));
			}
			if (userId.equals(3L)) {
				return Arrays.asList(new Conversation(Sets.newHashSet(new User(3L), new User(4L)), new Date()));
			}
			if (userId.equals(5L)) {
				return Arrays.asList(new Conversation(Sets.newHashSet(new User(5L), new User(6L)), new Date()));
			}
			if (userId.equals(7L)) {
				return Arrays.asList(new Conversation(Sets.newHashSet(new User(7L), new User(8L)), new Date()));
			}
			if (userId.equals(9L)) {
				return Arrays.asList(new Conversation(Sets.newHashSet(new User(9L), new User(10L)), new Date()));
			}
			if (userId.equals(11L)) {
				return Arrays.asList(new Conversation(Sets.newHashSet(new User(11L), new User(12L)), new Date()));
			}
			if (userId.equals(13L)) {
				return Arrays.asList(new Conversation(Sets.newHashSet(new User(13L), new User(14L)), new Date()),
									 new Conversation(Sets.newHashSet(new User(13L), new User(15L)), new Date()));
			}
			return null;
		}

		@Override
		public Map<Conversation, List<ConversationMessage>> getMessages(List<Conversation> conversations) {
			if (conversations.size() == 1) {
				Conversation conversation = conversations.get(0);
				if (conversation.getUserIds().contains(1L) && conversation.getUserIds().contains(2L)) {
					return ImmutableMap.of(conversation, Arrays.asList(
							new ConversationTextMessage(new User(1L), conversation.getId(), new Date(), "Hola",
														"DUMMY",
														false)));
				}
				if (conversation.getUserIds().contains(3L) && conversation.getUserIds().contains(4L)) {
					return ImmutableMap.of(conversation, Arrays.asList(
							new ConversationTextMessage(new User(3L), conversation.getId(), new Date(), "Hola",
														"DUMMY",
														false)));
				}
				if (conversation.getUserIds().contains(5L) && conversation.getUserIds().contains(6L)) {
					return ImmutableMap.of(conversation, Arrays.asList(
							new ConversationTextMessage(new User(5L), conversation.getId(), new Date(), "Hola",
														"DUMMY",
														false)));
				}
				if (conversation.getUserIds().contains(7L) && conversation.getUserIds().contains(8L)) {
					return ImmutableMap.of(conversation, Arrays.asList(
							new ConversationTextMessage(new User(7L), conversation.getId(), new Date(), "Hola",
														"DUMMY",
														false)));
				}
				if (conversation.getUserIds().contains(9L) && conversation.getUserIds().contains(10L)) {
					return ImmutableMap.of(conversation, Arrays.asList(
							new ConversationTextMessage(new User(9L), conversation.getId(), new Date(), "Hola",
														"DUMMY",
														false),
							new ConversationTextMessage(new User(9L), conversation.getId(), new Date(), "Hola 2",
														"DUMMY", false),
							new ConversationTextMessage(new User(10L), conversation.getId(), new Date(), "Hola 3",
														"DUMMY", false)));
				}
				if (conversation.getUserIds().contains(11L) && conversation.getUserIds().contains(12L)) {
					return ImmutableMap.of(conversation, Arrays.asList(
							new ConversationTextMessage(new User(10L), conversation.getId(), new Date(), "Hola",
														"DUMMY", false),
							new ConversationTextMessage(new User(10L), conversation.getId(), new Date(), "Hola 2",
														"DUMMY", false),
							new ConversationTextMessage(new User(11L), conversation.getId(), new Date(), "Hola 3",
														"DUMMY", false)));
				}
			}
			if (conversations.size() == 2) {
				Conversation conversation1 = conversations.get(0);
				Conversation conversation2 = conversations.get(1);
				if (conversation1.getUserIds().contains(13L) && conversation1.getUserIds().contains(14L) &&
						conversation2.getUserIds().contains(13L) && conversation2.getUserIds().contains(15L)) {
					return ImmutableMap.of(conversation1, Arrays.asList(
							new ConversationTextMessage(new User(13L), conversation1.getId(), new Date(), "Hola",
														"DUMMY", false),
							new ConversationTextMessage(new User(14L), conversation1.getId(), new Date(), "Hola 2",
														"DUMMY", false),
							new ConversationTextMessage(new User(14L), conversation1.getId(), new Date(), "Hola 3",
														"DUMMY", false)), conversation2, Arrays.asList(
							new ConversationTextMessage(new User(15L), conversation2.getId(), new Date(), "Hola",
														"DUMMY", false),
							new ConversationTextMessage(new User(13L), conversation2.getId(), new Date(), "Hola 2",
														"DUMMY", false),
							new ConversationTextMessage(new User(15L), conversation2.getId(), new Date(), "Hola 3",
														"DUMMY", false)));
				}
			}
			return null;
		}

		@Override
		public Map<Long, Set<String>> getApplications(Set<Long> userIds) {
			if (userIds.contains(1L) && userIds.contains(2L)) {
				return ImmutableMap.of(1L, Sets.newHashSet("ANGRY_WORDS"), 2L, Sets.newHashSet("ANGRY_WORDS"));
			}
			if (userIds.contains(3L) && userIds.contains(4L)) {
				return ImmutableMap.of(3L, Sets.newHashSet("ANGRY_WORDS", "WORD_CRACK"), 4L,
									   Sets.newHashSet("ANGRY_WORDS", "WORD_CRACK"));
			}
			if (userIds.contains(5L) && userIds.contains(6L)) {
				return ImmutableMap.of(5L, Sets.newHashSet("ANGRY_WORDS"), 6L, Sets.newHashSet("WORD_CRACK"));
			}
			if (userIds.contains(7L) && userIds.contains(8L)) {
				return ImmutableMap.of(7L, Sets.newHashSet("ANGRY_WORDS", "TRIVIA_CRACK"), 8L,
									   Sets.newHashSet("WORD_CRACK", "ANGRY_WORDS", "CHANNELS"));
			}
			if (userIds.contains(9L) && userIds.contains(10L)) {
				return ImmutableMap.of(9L, Sets.newHashSet("ANGRY_WORDS"), 10L, Sets.newHashSet("ANGRY_WORDS"));
			}
			if (userIds.contains(11L) && userIds.contains(12L)) {
				return ImmutableMap.of(11L, Sets.newHashSet("ANGRY_WORDS", "TRIVIA_CRACK"), 12L,
									   Sets.newHashSet("ANGRY_WORDS", "TRIVIA_CRACK"));
			}
			if (userIds.contains(13L) && userIds.contains(14L) && userIds.contains(15L)) {
				return ImmutableMap.of(13L, Sets.newHashSet("ANGRY_WORDS", "TRIVIA_CRACK", "SARASA"), 14L,
									   Sets.newHashSet("ANGRY_WORDS", "TRIVIA_CRACK", "SARASA2"), 15L,
						Sets.newHashSet("ANGRY_WORDS", "TRIVIA_CRACK", "SARASA2"));
			}
			return null;
		}

		@Override
		public Boolean checkAndSetMigration(Long userId) {
			return false;
		}
	}

}
