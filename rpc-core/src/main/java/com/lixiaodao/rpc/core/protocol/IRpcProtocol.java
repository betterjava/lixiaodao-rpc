package com.lixiaodao.rpc.core.protocol;

import com.lixiaodao.rpc.core.bytebuffer.RpcByteBuffer;

public interface IRpcProtocol {

	RpcByteBuffer encode(Object message, RpcByteBuffer wrapper) throws Exception;

	Object decode(RpcByteBuffer wrapper, Object errorObject, int... originPos) throws Exception;

}
