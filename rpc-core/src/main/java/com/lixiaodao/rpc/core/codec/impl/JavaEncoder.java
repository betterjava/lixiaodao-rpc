/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;

import java.io.ByteArrayOutputStream;

import java.io.ObjectOutputStream;

import com.lixiaodao.rpc.core.codec.RpcEncoder;
public class JavaEncoder implements RpcEncoder {

	@Override
	public byte[] encode(Object object) throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(byteArray);
		output.writeObject(object);
		output.flush();
		output.close();
		return byteArray.toByteArray(); 
	}

}
