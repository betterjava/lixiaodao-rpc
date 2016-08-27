package com.lixiaodao.registry.server;

import com.lixiaodao.registry.factory.RpcRegistryFactory;

public class RpcServerRegistry {

	private RpcServerRegistry() {

	}

	private static class SingletonHolder {
		static final RpcServerRegistry instance = new RpcServerRegistry();
	}

	public static RpcServerRegistry getInstance() {
		return SingletonHolder.instance;
	}

	public void registerClient(String server, String client) throws Exception {
		RpcRegistryFactory.getRpcRegistryServer().registerClient(server, client);
	}

	public void close() throws Exception {
		RpcRegistryFactory.getRpcRegistryServer().close();
	}

	public void connectZookeeper(String zkAddress, int timeout) throws Exception {
		RpcRegistryFactory.getRpcRegistryServer().connectZookeeper(zkAddress, timeout);
	}

	public void registerServer(String group, String server) throws Exception {
		RpcRegistryFactory.getRpcRegistryServer().registerServer(group, server);
	}

}
