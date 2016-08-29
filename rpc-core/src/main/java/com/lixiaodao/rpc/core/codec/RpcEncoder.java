package com.lixiaodao.rpc.core.codec;

public interface RpcEncoder {
	
	byte[] encode(Object obj) throws Exception;
	
}
