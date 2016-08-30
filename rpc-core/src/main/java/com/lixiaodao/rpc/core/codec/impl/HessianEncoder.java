/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;

import java.io.ByteArrayOutputStream;

import com.caucho.hessian.io.Hessian2Output;
import com.lixiaodao.rpc.core.codec.RpcEncoder;

public class HessianEncoder implements RpcEncoder {

	@Override
	public byte[] encode(Object object) throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		Hessian2Output output = new Hessian2Output(byteArray);
		output.writeObject(object);
		output.close();
		byte[] bytes = byteArray.toByteArray();
		return bytes;
	}

}
