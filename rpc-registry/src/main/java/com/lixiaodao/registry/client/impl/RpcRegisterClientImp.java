package com.lixiaodao.registry.client.impl;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.lixiaodao.registry.client.IRpcRegistryClient;

public class RpcRegisterClientImp implements IRpcRegistryClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcRegisterClientImp.class);

	// 标志
	private ConcurrentHashMap<String, Boolean> flags = new ConcurrentHashMap<>();

	// group 和 server 的对应关系
	private Map<String, Set<InetSocketAddress>> servers = new ConcurrentHashMap<>();

	private CuratorFramework client;

	@Override
	public Set<InetSocketAddress> getServerByGroup(String group) throws Exception {
		if (!flags.containsKey(group)) {
			
		}
		return null;
	}

	@Override
	public void close() throws Exception {
		servers.clear();
		client.close();
	}

	@Override
	public void connectZookeeper(String zkAdress, int timeout) throws Exception {
		// zk 地址，session 超时时间，连接超时时间，重连策略（当前的这种重连时间间隔是动态的）
		client = CuratorFrameworkFactory.newClient(zkAdress, timeout, timeout, new ExponentialBackoffRetry(1000, 3));
		client.start();
	}

	/**
	 * 获取 某个节点下 所有节点的 value
	 * 
	 * @param node
	 * @return
	 */
	private Map<String, String> listChildredDetail(String node) {
		Map<String, String> map = Maps.newHashMap();
		try {
			GetChildrenBuilder childredBuilder = getClient().getChildren();
			List<String> children = childredBuilder.forPath(node);
			GetDataBuilder dataBuilder = getClient().getData();
			if (children != null) {
				for (String child : children) {
					String propPath = ZKPaths.makePath(node, child);
					map.put(child, new String(dataBuilder.forPath(propPath), Charsets.UTF_8));
				}
			}

		} catch (Exception e) {
			LOGGER.error("listChildrenDetail fail", e);
		}
		return map;
	}

	private CuratorFramework getClient() {
		return client;
	}

}
