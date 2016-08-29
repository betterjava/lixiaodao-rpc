package com.lixiaodao.rpc.tcp.nett4.codec;

import java.util.List;

import com.lixiaodao.rpc.core.bytebuffer.RpcByteBuffer;
import com.lixiaodao.rpc.core.protocol.RpcProtocols;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcDecoderHander extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		
		RpcByteBuffer bytebufferWrapper = new com.lixiaodao.rpc.tcp.nett4.codec.RpcByteBuffer(ctx);
		Object obj = RpcProtocols.decode(bytebufferWrapper , null);
		if(obj != null){
			out.add(obj);
		}
	}

}
