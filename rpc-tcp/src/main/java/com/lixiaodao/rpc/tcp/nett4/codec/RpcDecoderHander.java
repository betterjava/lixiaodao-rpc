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
		
		RpcByteBuffer bytebufferWrapper = new com.lixiaodao.rpc.tcp.nett4.codec.RpcByteBuffer(in);
		// 因为 bytebuffer 是和channel 绑定的，也就是说同一个 channel 的读 或者写都是串行的，只需要粘包就可以，不够就不读，但是记得要 重新设定读的位置
		Object obj = RpcProtocols.decode(bytebufferWrapper , null);
		if(obj != null){
			out.add(obj);
		}
	}

}
