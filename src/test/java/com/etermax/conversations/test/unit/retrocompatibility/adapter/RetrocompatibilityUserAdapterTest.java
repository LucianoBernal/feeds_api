package com.etermax.conversations.test.unit.retrocompatibility.adapter;

import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.retrocompatibility.adapter.RetrocompatibilityUserAdapter;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityUserService;
import com.google.common.collect.Sets;
import dto.UserDTO;
import org.junit.Test;
import retrocompatibility.dto.RetrocompatibilityUserDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RetrocompatibilityUserAdapterTest {

	@Test
	public void getReceiversTest() {
		//Given
		RetrocompatibilityUserService retrocompatibilityUserService = mock(RetrocompatibilityUserService.class);
		when(retrocompatibilityUserService.getUsers(eq(Collections.singletonList(2l)), anyLong()))
				.thenReturn(Collections.singletonList(mock(RetrocompatibilityUserDTO.class)));
		RetrocompatibilityUserAdapter retrocompatibilityUserAdapter = new RetrocompatibilityUserAdapter(
				retrocompatibilityUserService);

		//When
		ConversationDisplayDTO conversationDisplayDTO = mock(ConversationDisplayDTO.class);
		when(conversationDisplayDTO.getUsers()).thenReturn(Sets.newHashSet(1l, 2l));
		List<RetrocompatibilityUserDTO> receivers = retrocompatibilityUserAdapter
				.getReceivers(1l, Collections.singletonList(conversationDisplayDTO));

		//Then
		assertThat(receivers).isNotNull();
	}

	@Test
	public void getReceiverTest() {
		//Given
		RetrocompatibilityUserService retrocompatibilityUserService = mock(RetrocompatibilityUserService.class);
		when(retrocompatibilityUserService.getUser(1l, 2l)).thenReturn(mock(UserDTO.class));
		RetrocompatibilityUserAdapter retrocompatibilityUserAdapter = new RetrocompatibilityUserAdapter(
				retrocompatibilityUserService);

		//When
		UserDTO user = retrocompatibilityUserAdapter.getUser(Arrays.asList(1l, 2l));

		//Then
		assertThat(user).isNotNull();
	}
}
