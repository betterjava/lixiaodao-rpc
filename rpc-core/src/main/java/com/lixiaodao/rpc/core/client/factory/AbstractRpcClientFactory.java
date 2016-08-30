package com.lixiaodao.rpc.core.client.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lixiaodao.rpc.core.client.AbstractRpcClient;
import com.lixiaodao.rpc.core.client.RpcClient;
import com.lixiaodao.rpc.core.message.RpcResponse;

public abstract class AbstractRpcClientFactory implements RpcClientFactory {

	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractRpcClientFactory.class);

	protected static ConcurrentHashMap<Integer, LinkedBlockingQueue<RpcResponse>> responses = new ConcurrentHashMap<Integer, LinkedBlockingQueue<RpcResponse>>();
	protected static Map<String, AbstractRpcClient> rpcClients = new ConcurrentHashMap<String, AbstractRpcClient>();

	@Override
	public void receiveResponse(RpcResponse response) throws Exception {

		if (!responses.containsKey(response.getRequestId())) {
			LOGGER.error("give up the response,request id is:" + response.getRequestId() + ",maybe because timeout!");
			return;
		}
		try {

			if (responses.containsKey(response.getRequestId())) {

				LinkedBlockingQueue<RpcResponse> queue = responses.get(response.getRequestId());
				if (queue != null) {
					queue.put(response);
				} else {
					LOGGER.warn(
							"give up the response,request id is:" + response.getRequestId() + ",because queue is null");
				}
			}

		} catch (InterruptedException e) {
			LOGGER.error("put response error,request id is:" + response.getRequestId(), e);
		}
	}

	@Override
	public RpcClient getClient(String host, int port) throws Exception {
		/**
		 * 从zk 去获取，但是要做本地缓存
		 */
		String key = "/" + host + ":" + port;
		if (rpcClients.containsKey(key)) {
			return rpcClients.get(key);
		}
		return createClient(host, port);
	}

	protected abstract RpcClient createClient(String targetIP, int targetPort) throws Exception;

	public  void putClient(String key,AbstractRpcClient client) {
			rpcClients.put(key, client);
	}
	
	@Override
	public void putResponse(int reqeustId, LinkedBlockingQueue<RpcResponse> queue) {
		responses.put(reqeustId, queue);
	}

	@Override
	public void removeResponse(int requestId) {
		responses.remove(requestId);
	}

	@Override
	public void removeRpcClient(String key) {
		rpcClients.remove(key);
	}
}
