/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;

import java.io.ByteArrayInputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.lixiaodao.rpc.core.codec.RpcDecoder;

/**
 *
 */
public class HessianDecoder implements RpcDecoder {

	/* (non-Javadoc)
	 * @see com.jd.cross.plateform.rocketrpc.core.codec.RocketRPCDecoder#decode(java.lang.String, byte[])
	 */
	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		// TODO Auto-generated method stub
		Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(bytes));
		Object resultObject = input.readObject();
		input.close();
		return resultObject;
	}

}
