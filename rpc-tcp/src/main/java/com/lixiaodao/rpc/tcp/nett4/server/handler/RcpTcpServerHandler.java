package com.lixiaodao.rpc.tcp.nett4.server.handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lixiaodao.registry.server.RpcServerRegistry;
import com.lixiaodao.rpc.core.message.RpcRequest;
import com.lixiaodao.rpc.core.message.RpcResponse;
import com.lixiaodao.rpc.core.server.handler.factory.RpcServiceHandlerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class RcpTcpServerHandler extends ChannelInboundHandlerAdapter{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RcpTcpServerHandler.class);
	
	private ThreadPoolExecutor threadPoolExecutor;
	
	private int port;
	
	private int procotolType;//协议名称
	
	private int codecType;//编码类型
	
	public RcpTcpServerHandler(int threadCount,int port,int procotoltype,int codectype){
		this.port = port;
		this.procotolType = procotoltype;
		this.codecType = codectype;
		this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 当有客户端连接的时候 执行，注册sever-client 到 zk
		RpcServerRegistry.getInstance().registerClient(getLocalhost(), ctx.channel().remoteAddress().toString());
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 客户端断开连接是调用,关闭 channle 
		ctx.channel().close();
	}
	
	/**
	 * 主处理逻辑，在接受到客户端请求时调用，组包处理已经在encode 处理掉了
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof RpcRequest)) {
			LOGGER.error("receive message error,only support RequestWrapper");
			throw new Exception("receive message error,only support RequestWrapper || List");
		}
		threadPoolExecutor.submit(new ServerHandlerRunnable(ctx,msg));
	}
	
	// 发生异常是调用，关闭 channel，释放资源
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(!(cause instanceof IOException)){
			 LOGGER.error("catch some exception not IOException", cause);
		}
		ctx.channel().close();
	}
	
	private void handleRequestWithSingleThread(final ChannelHandlerContext ctx, Object message) {
		RpcResponse response = null;
		try {
			RpcRequest request = (RpcRequest) message;
			
			response = RpcServiceHandlerFactory.getTcpServerHandler().handleRequest(request, codecType, procotolType);
			
			if (ctx.channel().isOpen()) {
				ChannelFuture writeFuture = ctx.channel().writeAndFlush(response);
				writeFuture.addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							LOGGER.error("server write response error,client  host is: "
									+ ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName() + ":"
									+ ((InetSocketAddress) ctx.channel().remoteAddress()).getPort() + ",server Ip:"
									+ getLocalhost());
							ctx.channel().close();
						}
					}
				});
			}
		} catch (Exception e) {
			// 出现异常的时候，要把异常信息发送给客户端
			sendErrorResponse(ctx, (RpcRequest) message, e.getMessage() + ",server Ip:" + getLocalhost());
		} finally {
			ReferenceCountUtil.release(message);
		}
	}
	private void sendErrorResponse(final ChannelHandlerContext ctx, final RpcRequest request,String errorMessage) {
		RpcResponse response = new RpcResponse(request.getId(), request.getCodecType(), request.getProtocolType());
		response.setException(new Exception(errorMessage));
		
		ChannelFuture writeFuture = ctx.channel().writeAndFlush(response);
		writeFuture.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(!future.isSuccess()){
					// 每次写入都要有lister，失败的时候要释放资源.
					LOGGER.error("server write response error,request id is: " + request.getId()+",client Ip is:"+ctx.channel().remoteAddress().toString()+",server Ip:"+getLocalhost());
			        ctx.channel().close();
				}
			}
		});
	}
	
	
	private String getLocalhost(){
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip+":"+port;
		} catch (UnknownHostException e) {
			throw new RuntimeException("无法获取本地Ip",e);
		}
		
	}
	
	
	private class ServerHandlerRunnable implements  Runnable{
		
		private ChannelHandlerContext ctx;
		private Object msg;
		
		public ServerHandlerRunnable(ChannelHandlerContext ctx, Object message) {
			super();
			this.ctx = ctx;
			this.msg = message;
		}
		
		public void run() {
			handleRequestWithSingleThread(ctx, msg);
		}
	}
	
}
