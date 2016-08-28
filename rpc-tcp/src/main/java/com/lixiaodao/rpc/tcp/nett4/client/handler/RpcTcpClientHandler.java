package com.lixiaodao.rpc.tcp.nett4.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcTcpClientHandler extends ChannelInboundHandlerAdapter {

	public RpcTcpClientHandler() {
		super();
	}
	
	
	/**
	 * 接收服务端的响应
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		super.channelRead(ctx, msg);
	}
}
