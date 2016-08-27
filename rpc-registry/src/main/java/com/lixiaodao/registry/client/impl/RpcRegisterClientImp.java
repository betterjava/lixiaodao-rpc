package com.lixiaodao.registry.client.impl;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
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
	public Set<InetSocketAddress> getServerByGroup(final String group) throws Exception {
		if (!flags.containsKey(group)) {

			ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

			@SuppressWarnings("resource")
			PathChildrenCache childrenCache = new PathChildrenCache(client, "/" + group, true);
			childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
			childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
					if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {// 监听子节点被删除的情况

						String path = event.getData().getPath();
						String[] nodes = path.split("/");// 1:group 2:address
						if (nodes.length > 0 && nodes.length == 3) {
							updateServerList(nodes[1], nodes[2]);
						}

					} else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {// 监听增加
						String path = event.getData().getPath();
						String[] nodes = path.split("/");// 1:group 2:address
						if (nodes.length > 0 && nodes.length == 3) {
							Map<String, String> valueMap = listChildrenDetail("/" + nodes[1]);
							for (String value : valueMap.values()) {
								String[] nodes1 = value.split(":");
								InetSocketAddress socketAddress = new InetSocketAddress(nodes1[0],
										Integer.parseInt(nodes1[1]));
								Set<InetSocketAddress> addresses = servers.get(group);
								addresses.add(socketAddress);
								servers.put(group, addresses);
							}

						}
					}
				}
			}, pool);
			flags.put(group, true);
		}

		if (servers.containsKey(group)) {
			return servers.get(group);
		}
		Set<InetSocketAddress> addresses = new HashSet<>();
		Map<String, String> maps = listChildrenDetail("/" + group);
		if (maps != null && maps.size() > 0) {
			for (String value : maps.values()) {
				String[] host = value.split(":");
				addresses.add(new InetSocketAddress(host[0], Integer.parseInt(host[1])));
			}
			servers.put(group, addresses);
		}

		return addresses;
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
	private Map<String, String> listChildrenDetail(String node) {
		Map<String, String> map = Maps.newHashMap();
		try {
			GetChildrenBuilder childrenBuilder = getClient().getChildren();
			List<String> children = childrenBuilder.forPath(node);
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

	private void updateServerList(String group, String server) throws Exception {
		if (servers.containsKey(group)) {
			Set<InetSocketAddress> rpcServer = servers.get(group);
			Set<InetSocketAddress> newrpcservers = new HashSet<InetSocketAddress>();
			for (InetSocketAddress socketAddress : rpcServer) {
				String oldServer = socketAddress.getAddress().toString() + ":" + socketAddress.getPort();
				String oldServerWithoutPrefix = oldServer.substring(1, oldServer.length());
				if (!server.startsWith(oldServerWithoutPrefix)) {
					newrpcservers.add(socketAddress);
				} else {// 删除
					deleteNode(oldServer);
				}
			}

		}
	}

	// 删除节点
	private void deleteNode(String path) throws Exception {
		try {
			Stat stat = getClient().checkExists().forPath(path);
			if (stat != null) {
				getClient().delete().deletingChildrenIfNeeded().forPath(path);
			}
		} catch (Exception e) {
			// LOGGER.error("deleteNode fail", e);
		}
	}

	private CuratorFramework getClient() {
		return client;
	}

}
