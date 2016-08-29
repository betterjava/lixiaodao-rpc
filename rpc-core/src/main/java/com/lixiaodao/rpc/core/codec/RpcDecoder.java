package com.lixiaodao.rpc.core.codec;

public interface RpcDecoder {

	Object decode(String className, byte[] bytes) throws Exception;
}
