package com.lixiaodao.registry.client;

import java.net.InetSocketAddress;
import java.util.Set;

import com.lixiaodao.registry.factory.RpcRegistryFactory;

public class RpcClientRegistry {

	private RpcClientRegistry() {

	}

	private static class SingletonHolder {
		static final RpcClientRegistry instance = new RpcClientRegistry();
	}

	public static RpcClientRegistry getInstance() {
		return SingletonHolder.instance;
	}

	public Set<InetSocketAddress> getServerByGroup(String group) throws Exception {
		return RpcRegistryFactory.getRpcRegistryClient().getServerByGroup(group);
	}

	public void close() throws Exception {
		RpcRegistryFactory.getRpcRegistryClient().close();
	}

	public void connectZookeeper(String zkAdress, int timeout) throws Exception {
		RpcRegistryFactory.getRpcRegistryClient().connectZookeeper(zkAdress, timeout);
	}
}
