package com.etermax.conversations.test.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ GetConversationMessagesTest.class, GetConversationTest.class, SyncMessagesTest.class,
		MessageDeleteTest.class, SaveConversationSendMessageTest.class, SaveConversationTest.class,
		SaveMessageTest.class, DeleteConversationTest.class })

public class IntegrationTestSuite {

	public class JunitTestSuite {
	}

}
