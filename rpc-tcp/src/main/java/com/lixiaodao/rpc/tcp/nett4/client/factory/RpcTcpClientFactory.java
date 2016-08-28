package com.lixiaodao.rpc.tcp.nett4.client.factory;

import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lixiaodao.rpc.core.client.factory.AbstractRpcClientFactory;
import com.lixiaodao.rpc.core.thread.NamedThreadFactory;
import com.lixiaodao.rpc.tcp.nett4.client.handler.RpcClientHandler;
import com.lixiaodao.rpc.tcp.nett4.codec.RpcDecoderHander;
import com.lixiaodao.rpc.tcp.nett4.codec.RpcEncoderHander;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class RpcTcpClientFactory extends AbstractRpcClientFactory {

	private final static RpcTcpClientFactory _self = new RpcTcpClientFactory();

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpClientFactory.class);
	
	private final static ThreadFactory workerThreadFactory = new NamedThreadFactory("NETTYCLIENT-WORKER-");
	
	private static final EventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 6 ,workerThreadFactory);
	
	private static final Bootstrap bootstrap = new Bootstrap();
	

	@Override
	public void startClient(int timeout) {
		LOGGER.info("----------------客户端开始启动-------------------------------");
		bootstrap.group(workGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
	        .option(ChannelOption.TCP_NODELAY, true)
	        .option(ChannelOption.SO_REUSEADDR,true)
	        .option(ChannelOption.SO_KEEPALIVE, true)
		 	.option(ChannelOption.SO_SNDBUF, 65535)
		 	.option(ChannelOption.SO_RCVBUF, 65535);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast("decoder", new RpcDecoderHander());
    			pipeline.addLast("encoder", new RpcEncoderHander());
    			pipeline.addLast("timeout",new IdleStateHandler(0, 0, 120));
    			pipeline.addLast("handler",new RpcClientHandler());
			
			}
			
		});
		LOGGER.info("----------------客户端启动结束-------------------------------");
	}

	@Override
	public void stopClinet() {
		// TODO Auto-generated method stub

	}

	public static RpcTcpClientFactory getInstance() {
		return _self;
	}

}
