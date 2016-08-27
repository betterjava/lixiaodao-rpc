package com.lixiaodao.registry.factory;

import com.lixiaodao.registry.client.IRpcRegistryClient;
import com.lixiaodao.registry.client.impl.RpcRegisterClientImp;
import com.lixiaodao.registry.server.IRpcRegistryServer;
import com.lixiaodao.registry.server.impl.RpcRegistryServerImpl;

public class RpcRegistryFactory {

	private static IRpcRegistryServer server = new RpcRegistryServerImpl();

	private static IRpcRegistryClient client = new RpcRegisterClientImp();

	public static IRpcRegistryServer getRpcRegistryServer() {
		return server;
	}

	public static IRpcRegistryClient getRpcRegistryClient() {
		return client;
	}
}
