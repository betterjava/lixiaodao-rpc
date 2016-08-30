/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;
import com.esotericsoftware.kryo.io.Input;
import com.lixiaodao.rpc.core.codec.RpcDecoder;
import com.lixiaodao.rpc.core.util.KryoUtils;

public class KryoDecoder implements RpcDecoder {


	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		Input input = new Input(bytes);
		return KryoUtils.getKryo().readClassAndObject(input);
	}

}
