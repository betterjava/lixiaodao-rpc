package com.lixiaodao.rpc.tcp.nett4.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lixiaodao.rpc.core.server.RpcServer;
import com.lixiaodao.rpc.core.server.handler.factory.RpcServiceHandlerFactory;
import com.lixiaodao.rpc.core.thread.NamedThreadFactory;
import com.lixiaodao.rpc.server.filter.RpcFilter;
import com.lixiaodao.rpc.tcp.nett4.codec.RpcDecoderHander;
import com.lixiaodao.rpc.tcp.nett4.codec.RpcEncoderHander;
import com.lixiaodao.rpc.tcp.nett4.server.handler.RcpTcpServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class RpcTcpServer implements RpcServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpServer.class);

	private static final int PROCESSORS = Runtime.getRuntime().availableProcessors() * 2;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private int procotolType;// 协议名称

	private int codecType;// 编码类型

	private int threadCount;// 线程数

	private static class SingletonHolder {
		static final RpcTcpServer instance = new RpcTcpServer();
	}

	public static RpcTcpServer getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public void registerService(String serviceName, Object targetInstance, RpcFilter rpcFilter) {
		RpcServiceHandlerFactory.getTcpServerHandler().registrerService(serviceName, targetInstance, rpcFilter);
	}

	@Override
	public void start(int port, int timeout) throws Exception {
		ThreadFactory serverBossTF = new NamedThreadFactory("NETTYSERVER-BOSS-");
		ThreadFactory serverWorkerTF = new NamedThreadFactory("NETTYSERVER-WORKER-");
		bossGroup = new NioEventLoopGroup(PROCESSORS, serverBossTF);
		workerGroup = new NioEventLoopGroup(PROCESSORS * 2, serverWorkerTF);
		ServerBootstrap bootstrap = new ServerBootstrap();
		
		 bootstrap.group(bossGroup, workerGroup)
	        .channel(NioServerSocketChannel.class)
	        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
	        .option(ChannelOption.SO_BACKLOG, 1024)
	        .option(ChannelOption.SO_REUSEADDR,true)
	        .option(ChannelOption.SO_KEEPALIVE, true)
		 	.option(ChannelOption.SO_SNDBUF, 65535)
		 	.option(ChannelOption.SO_RCVBUF, 65535)
		 	.childOption(ChannelOption.TCP_NODELAY, true);
	   bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

	        protected void initChannel(SocketChannel channel) throws Exception {
	          ChannelPipeline pipeline = channel.pipeline();
	          pipeline.addLast("decoder", new RpcDecoderHander());
	          pipeline.addLast("encoder", new RpcEncoderHander());
	          pipeline.addLast("timeout",new IdleStateHandler(0, 0, 120));
	          pipeline.addLast("handler", new RcpTcpServerHandler());
	        }
	      });
	   
	   LOGGER.info("-----------------开始启动--------------------------");
	   bootstrap.bind(new InetSocketAddress(port)).sync();
	   LOGGER.info("端口号："+port+"的服务端已经启动,作者:cookie");
	   LOGGER.info("-----------------启动结束--------------------------");

	}

	@Override
	public void stop() throws Exception {
		RpcServiceHandlerFactory.getTcpServerHandler().clear();
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	
	public int getProcotolType() {
		return procotolType;
	}

	public void setProcotolType(int procotolType) {
		this.procotolType = procotolType;
	}

	public int getCodecType() {
		return codecType;
	}

	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

}
