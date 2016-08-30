package com.lixiaodao.rpc.core.protocol;

import com.lixiaodao.rpc.core.bytebuffer.RpcByteBuffer;
import com.lixiaodao.rpc.core.message.RpcRequest;
import com.lixiaodao.rpc.core.message.RpcResponse;
import com.lixiaodao.rpc.core.protocol.factory.RpcProtocolFactory;

public class RpcProtocols {

	public static final int HEADER_LEN = 2;// 版本号 和 type 两个字节

	public static final byte CURRENT_VERSION = (byte) 1;

	public static RpcByteBuffer encode(Object message, RpcByteBuffer bytebufferWrapper) throws Exception {
		Integer type = 0;
		// 因为发送是自己人为控制的，所有不需要判断字节
		if (message instanceof RpcRequest) {
			type = ((RpcRequest) message).getProtocolType();
		} else if (message instanceof RpcResponse) {
			type = ((RpcResponse) message).getProtocolType();
		}
		return RpcProtocolFactory.getProtocol(type).encode(message, bytebufferWrapper);
	}

	public static Object decode(RpcByteBuffer bytebufferWrapper, Object errorObject) throws Exception {
		// 因为 绑定在 channel 的buffer 是串行的，所以 originPos 只能是零
		final int originPos = bytebufferWrapper.readerIndex();
		if (bytebufferWrapper.readableBytes() < 2) {
			bytebufferWrapper.setReaderIndex(originPos);
			return errorObject;
		}
		int version = bytebufferWrapper.readByte();
		if(version == 1){
			int type = bytebufferWrapper.readByte();
			IRpcProtocol protocol = RpcProtocolFactory.getProtocol(type);
			if(protocol == null){
				throw new Exception("Unsupport protocol type: "+type);
			}
			return RpcProtocolFactory.getProtocol(type).decode(bytebufferWrapper, errorObject, new int[]{originPos});
		}else{
			throw new Exception("Unsupport protocol version: "+ version);
		}
		

	}

}
