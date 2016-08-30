package com.lixiaodao.registry.server;

import com.lixiaodao.registry.factory.RpcRegistryFactory;

/**
 * server 端的逻辑
 * 
 * 1.注册 group-server 一对多
 * 2.注册 server-client 一对多
 * 3.client 通过group 找到 对应的server 列表
 * @author lijia
 *
 */
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
