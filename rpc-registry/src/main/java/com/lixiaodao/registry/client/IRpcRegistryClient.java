package com.lixiaodao.registry.client;

import java.net.InetSocketAddress;
import java.util.Set;

public interface IRpcRegistryClient {

	/**
	 * 根据 组名 获取 proverider 的网络地址列表
	 * 
	 * @param group
	 * @return
	 * @throws Exception
	 */
	Set<InetSocketAddress> getServerByGroup(String group) throws Exception;

	void close() throws Exception;

	/**
	 * 连接 zk
	 * 
	 * @param zkAdress
	 * @param timeout
	 * @throws Exception
	 */
	void connectZookeeper(String zkAdress, int timeout) throws Exception;

}
