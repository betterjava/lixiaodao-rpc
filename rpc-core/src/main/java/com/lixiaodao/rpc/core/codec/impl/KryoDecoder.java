/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;
import com.esotericsoftware.kryo.io.Input;
import com.lixiaodao.rpc.core.codec.RpcDecoder;
import com.lixiaodao.rpc.core.util.KryoUtils;

public class KryoDecoder implements RpcDecoder {

	/* (non-Javadoc)
	 * @see com.jd.cross.plateform.rocketrpc.core.codec.RocketRPCDecoder#decode(java.lang.String, byte[])
	 */
	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		// TODO Auto-generated method stub
		Input input = new Input(bytes);
		return KryoUtils.getKryo().readClassAndObject(input);
	}

}
