package com.etermax.conversations.test.unit;

import com.etermax.conversations.test.unit.dto.*;
import com.etermax.conversations.test.unit.factory.ConversationFactoryTest;
import com.etermax.conversations.test.unit.factory.UserFactoryTest;
import com.etermax.conversations.test.unit.model.ConversationTest;
import com.etermax.conversations.test.unit.model.MessageTest;
import com.etermax.conversations.test.unit.model.UserTest;
import com.etermax.conversations.test.unit.resource.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AddressedMessageCreationDTOTest.class, AddressedMessageDisplayDTOTest.class, AppInfoDTOTest.class,
		ConversationCreationDTOTest.class, ConversationDisplayDTOTest.class, ConversationMessageDeletionDTOTest.class,
		ConversationMessageDisplayDTOTest.class, ConversationFactoryTest.class, UserFactoryTest.class,
		ConversationTest.class, MessageTest.class, UserTest.class, ApplicationResourceTest.class,
		ConversationMessagesDeletionResourceTest.class, ConversationSyncMessagesResourceTest.class,
		ConversationResourceTest.class, ConversationsQueryResourceTest.class, MessagesResourceTest.class })
public class UnitTestSuite {

	public class JunitTestSuite {
	}
}
