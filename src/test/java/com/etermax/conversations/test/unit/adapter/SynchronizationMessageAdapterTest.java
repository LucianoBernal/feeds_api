package com.etermax.conversations.test.unit.adapter;

import com.etermax.conversations.adapter.SynchronizationAdapter;
import com.etermax.conversations.adapter.impl.SynchronizationAdapterImpl;
import com.etermax.conversations.error.ClientException;
import com.etermax.conversations.error.GetConversationMessagesException;
import com.etermax.conversations.error.InvalidConversationIdException;
import com.etermax.conversations.error.UserNotInConversationException;
import com.etermax.conversations.model.ConversationHistory;
import com.etermax.conversations.model.Range;
import com.etermax.conversations.retrocompatibility.migration.service.MigrationService;
import com.etermax.conversations.service.SynchronizationService;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SynchronizationMessageAdapterTest {

	@Test
	public void getConversationHistoryWithInvalidUserIdTest() {
		//GIVEN
		MigrationService migrationService = mock(MigrationService.class);
		SynchronizationAdapter adapter = new SynchronizationAdapterImpl(mock(SynchronizationService.class),
				migrationService);

		Throwable thrown = catchThrowable(() -> adapter.getConversationHistory("1", 1l, 1l, null, "A2"));
		//WHEN

		//THEN
		assertThat(thrown).isInstanceOf(ClientException.class);

	}

	@Test
	public void getConversationHistoryWithInvalidConverstaionIdTest() {
		//GIVEN
		MigrationService migrationService = mock(MigrationService.class);
		SynchronizationAdapter adapter = new SynchronizationAdapterImpl(mock(SynchronizationService.class),
				migrationService);

		//WHEN
		Throwable thrown = catchThrowable(() -> adapter.getConversationHistory(null, 1L, 1L, 1L, "A2"));

		//THEN
		assertThat(thrown).isInstanceOf(ClientException.class).hasCauseInstanceOf(InvalidConversationIdException.class);

	}

	@Test
	public void noUserIdGetUserReceiptsTest() {
		//Given
		MigrationService migrationService = mock(MigrationService.class);
		SynchronizationService synchronizationService = mock(SynchronizationService.class);
		when(synchronizationService.getConversationSync(anyLong(), anyString(), anyString())).thenReturn(mock(List.class));
		SynchronizationAdapter synchronizationAdapter = new SynchronizationAdapterImpl(synchronizationService,
				migrationService);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> synchronizationAdapter.getConversationSync(null, "1", "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class);
	}

	@Test
	public void invalidGetUserReceiptsTest() {
		//GIVEN
		MigrationService migrationService = mock(MigrationService.class);
		SynchronizationService synchronizationService = mock(SynchronizationService.class);
		when(synchronizationService.getConversationSync(anyLong(), anyString(), anyString())).thenThrow(mock(UserNotInConversationException.class));
		SynchronizationAdapterImpl synchronizationAdapter = new SynchronizationAdapterImpl(synchronizationService,
				migrationService);

		//WHEN
		ThrowableAssert.ThrowingCallable throwingCallable = () -> synchronizationAdapter.getConversationSync(1l, "0", "A2");

		//THEN
		assertThatThrownBy(throwingCallable).isInstanceOf(UserNotInConversationException.class);

	}

	@Test
	public void invalidGetDataHistoryTest() throws GetConversationMessagesException {
		//Given
		MigrationService migrationService = mock(MigrationService.class);
		SynchronizationService synchronizationService = mock(SynchronizationService.class);
		when(synchronizationService.getConversationHistory(anyString(), any(Range.class), anyLong(), anyString()))
				.thenThrow(GetConversationMessagesException.class);
		SynchronizationAdapter synchronizationAdapter = new SynchronizationAdapterImpl(synchronizationService,
				migrationService);

		//When
		ThrowableAssert.ThrowingCallable throwingCallable = () -> synchronizationAdapter
				.getConversationHistory("1", 1l, 1l, 1l, "A2");

		//Then
		assertThatThrownBy(throwingCallable).isInstanceOf(ClientException.class)
				.hasCauseInstanceOf(GetConversationMessagesException.class);
	}

	private SynchronizationAdapterImpl givenASynchronizationAdapter() throws GetConversationMessagesException {
		MigrationService migrationService = mock(MigrationService.class);
		SynchronizationService syncService = mock(SynchronizationService.class);
		when(syncService.getConversationHistory(anyString(), any(Range.class), anyLong(), anyString()))
				.thenReturn(mock(ConversationHistory.class));
		return new SynchronizationAdapterImpl(syncService, migrationService);
	}

}
