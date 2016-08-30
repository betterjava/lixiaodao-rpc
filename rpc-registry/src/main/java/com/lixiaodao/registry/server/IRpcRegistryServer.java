package com.lixiaodao.registry.server;

public interface IRpcRegistryServer {

	/**
	 * 把 provider 和 consumer 的网络地址信息 注册到 zk 上--在目前本本上好像没什么用 TODO
	 * 
	 * @param server
	 * @param client
	 * @throws Exception
	 */
	void registerClient(String server, String client) throws Exception;

	/**
	 * 关闭服务
	 * 
	 * @throws Exception
	 */
	void close() throws Exception;

	/**
	 * 连接 zk
	 * 
	 * @param zkAddress
	 *            zk 地址
	 * @param timeout
	 *            超时时间
	 * @throws Exception
	 */
	void connectZookeeper(String zkAddress, int timeout) throws Exception;

	/**
	 * 把提供者的 网络信息 和组 信息对应，注册到zk上
	 * 
	 * @param group
	 * @param server
	 * @throws Exception
	 */
	void registerServer(String group, String server) throws Exception;
}
