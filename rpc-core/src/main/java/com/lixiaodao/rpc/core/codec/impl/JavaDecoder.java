/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import com.lixiaodao.rpc.core.codec.RpcDecoder;

public class JavaDecoder implements RpcDecoder {

	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
		Object resultObject = objectIn.readObject();
		objectIn.close();
		return resultObject;
	}

}
