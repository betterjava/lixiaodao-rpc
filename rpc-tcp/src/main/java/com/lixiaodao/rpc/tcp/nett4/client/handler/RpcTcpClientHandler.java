package com.lixiaodao.rpc.tcp.nett4.client.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lixiaodao.rpc.core.message.RpcResponse;
import com.lixiaodao.rpc.tcp.nett4.client.factory.RpcTcpClientFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class RpcTcpClientHandler extends ChannelInboundHandlerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(RpcTcpClientHandler.class);

	public RpcTcpClientHandler() {
		super();
	}

	/**
	 * 接收服务端的响应
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		try {

			if (msg instanceof RpcResponse) {
				RpcResponse response = (RpcResponse) msg;
				if (LOGGER.isDebugEnabled()) {
					// for performance trace
					LOGGER.debug("receive response list from server: " + ctx.channel().remoteAddress() + ",request is:"
							+ response.getRequestId());
				}

				RpcTcpClientFactory.getInstance().receiveResponse(response);
			} else {
				LOGGER.error("receive message error,only support List || ResponseWrapper");
				throw new Exception("receive message error,only support List || ResponseWrapper");
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (!(cause instanceof IOException)) {
			LOGGER.error("catch some exception not IOException", cause);
		}
		// TODO 其他操作
		if (ctx.channel().isOpen()) {
			ctx.channel().close();
		}

	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		LOGGER.error("connection closed: " + ctx.channel().remoteAddress());
		// 其他 操作 TODO

		if (ctx.channel().isOpen()) {
			ctx.channel().close();
		}
	}
}
