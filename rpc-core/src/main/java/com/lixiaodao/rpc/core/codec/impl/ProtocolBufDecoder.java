/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;

import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.Message;
import com.lixiaodao.rpc.core.codec.RpcDecoder;
public class ProtocolBufDecoder implements RpcDecoder {
	
	private static ConcurrentHashMap<String, Message> messages = new ConcurrentHashMap<String, Message>();

	public static void addMessage(String className,Message message){
		messages.putIfAbsent(className, message);
	}
	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		Message message = messages.get(className);
		return message.newBuilderForType().mergeFrom(bytes).build();
	}

}
