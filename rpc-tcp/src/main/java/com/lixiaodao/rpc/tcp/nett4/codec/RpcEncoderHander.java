package com.lixiaodao.rpc.tcp.nett4.codec;

import com.lixiaodao.rpc.core.bytebuffer.RpcByteBuffer;
import com.lixiaodao.rpc.core.protocol.RpcProtocols;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoderHander extends MessageToByteEncoder {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		
		RpcByteBuffer bytebufferWrapper = new com.lixiaodao.rpc.tcp.nett4.codec.RpcByteBuffer(ctx);
		RpcProtocols.encode(msg, bytebufferWrapper );
	}
}
