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
	/* (non-Javadoc)
	 * @see com.jd.cross.plateform.rocketrpc.core.codec.RocketRPCDecoder#decode(java.lang.String, byte[])
	 */
	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		// TODO Auto-generated method stub
		Message message = messages.get(className);
		return message.newBuilderForType().mergeFrom(bytes).build();
	}

}
