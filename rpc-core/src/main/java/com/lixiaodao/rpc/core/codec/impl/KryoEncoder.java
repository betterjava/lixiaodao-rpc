/**
 * 
 */
package com.lixiaodao.rpc.core.codec.impl;

import com.esotericsoftware.kryo.io.Output;
import com.lixiaodao.rpc.core.codec.RpcEncoder;
import com.lixiaodao.rpc.core.util.KryoUtils;
public class KryoEncoder implements RpcEncoder {

	@Override
	public byte[] encode(Object object) throws Exception {
		Output output = new Output(256);
		KryoUtils.getKryo().writeClassAndObject(output, object);
		return output.toBytes();
	}

}
