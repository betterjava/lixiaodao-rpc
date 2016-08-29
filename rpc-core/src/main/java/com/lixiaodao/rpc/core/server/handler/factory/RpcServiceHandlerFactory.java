package com.lixiaodao.rpc.core.server.handler.factory;

import com.lixiaodao.rpc.core.server.handler.RpcServerHandler;
import com.lixiaodao.rpc.core.server.handler.impl.RpcTcpServerHandlerImpl;

public class RpcServiceHandlerFactory {
	private static RpcServerHandler rpcTcpServerHandler = new RpcTcpServerHandlerImpl();

	public static RpcServerHandler getTcpServerHandler() {
		return rpcTcpServerHandler;
	}
}
