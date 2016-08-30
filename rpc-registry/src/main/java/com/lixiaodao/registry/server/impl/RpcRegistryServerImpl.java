package com.lixiaodao.registry.server.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.lixiaodao.registry.server.IRpcRegistryServer;

public class RpcRegistryServerImpl implements IRpcRegistryServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcRegistryServerImpl.class);

	private CuratorFramework client;

	@Override
	public void registerClient(String server, String client) throws Exception {
		// 临时节点，client 断开后，直接给删掉
		this.createNode("/"+server+client, client, CreateMode.EPHEMERAL_SEQUENTIAL);
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

	@Override
	public void connectZookeeper(String zkAddress, int timeout) throws Exception {

		// zk 地址，session 超时时间，连接超时时间，重连策略（当前的这种重连时间间隔是动态的）
		client = CuratorFrameworkFactory.newClient(zkAddress, timeout, timeout, new ExponentialBackoffRetry(1000, 3));
		client.start();
	}

	@Override
	public void registerServer(String group, String server) throws Exception {
		/**
		 * 先创建 父级节点 ，再建立根节点，先检查后创建，保证不重复创建
		 */
		this.createNode("/" +group, group, CreateMode.PERSISTENT);
		this.createNode("/" +group+"/"+server, server, CreateMode.PERSISTENT_SEQUENTIAL);
	}

	/**
	 * 创建 zk 节点
	 * 
	 * @param nodeName
	 * @param value
	 * @param createMode
	 * @return
	 */
	private boolean createNode(String nodeName, String value, CreateMode createMode) {
		boolean success = false;
		try {
			Stat stat = this.getClient().checkExists().forPath(nodeName);
			if (stat == null) {
				String opResult = null;
				if (Strings.isNullOrEmpty(value)) {
					opResult = getClient().create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName);
				} else {
					opResult = getClient().create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName,
							value.getBytes(Charsets.UTF_8));
				}
				success = Objects.equal(nodeName, opResult);
			}

		} catch (Exception e) {
			LOGGER.error("createNode fail,path:" + nodeName, e);
		}

		return success;
	}

	private CuratorFramework getClient() {
		return client;
	}

}
