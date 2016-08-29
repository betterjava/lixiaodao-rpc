/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import com.lixiaodao.rpc.core.codec.RpcDecoder;

/**
 * @author liubing1
 * jdk 序列化
 */
public class JavaDecoder implements RpcDecoder {

	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		// TODO Auto-generated method stub
		ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
		Object resultObject = objectIn.readObject();
		objectIn.close();
		return resultObject;
	}

}
