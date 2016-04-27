package com.etermax.conversations.repository.impl.elasticsearch.dao.counter.impl;

import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;

import java.util.HashMap;

public class ElasticsearchCounterDAO {

//	private void incrementUnreadMessages(Long userId, String conversationId, String app) {
//
//		Script script = new Script("l=ctx._source.unreadMessages;n=0;l.collect{it.key==userApp?it.value++:n++};"
//										   + "n==l.size()?l<<[key:userApp,value:1]:l", ScriptService.ScriptType.INLINE,
//								   null, new HashMap<>());
//		script.getParams().put("userApp", userId.toString() + "-" + app);
//
//		client.prepareUpdate(indexName, "conversation", String.valueOf(conversationId))
//			  .setRouting(conversationId)
//			  .setScript(script)
//			  .setRetryOnConflict(10)
//			  .get();
//
//	}

//	public void resetRead(String conversationId, Long userId, String app) {
//
//		Script script = new Script("l=ctx._source.unreadMessages;n=0;l.collect{it.key==userApp?it.value=0:n++};"
//										   + "n==l.size()?l<<[key:userApp,value:0]:l", ScriptService.ScriptType.INLINE,
//								   null, new HashMap<>());
//		script.getParams().put("userApp", userId.toString() + "-" + app);
//
//		client.prepareUpdate(indexName, "conversation", String.valueOf(conversationId)).setScript(script).setRetryOnConflict(10).get();
//	}

}
