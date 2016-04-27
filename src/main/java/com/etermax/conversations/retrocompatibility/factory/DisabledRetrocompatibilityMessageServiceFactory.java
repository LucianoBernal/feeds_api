package com.etermax.conversations.retrocompatibility.factory;

import com.etermax.conversations.retrocompatibility.service.DisabledRetrocompatibilityMessageService;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityMessageService;

public class DisabledRetrocompatibilityMessageServiceFactory implements RetrocompatibilityMessageServiceFactory{
	@Override
	public RetrocompatibilityMessageService createMessageService() {
		return new DisabledRetrocompatibilityMessageService();
	}
}
