package com.lixiaodao.rpc.core.client.factory;

import java.util.concurrent.LinkedBlockingQueue;

import com.lixiaodao.rpc.core.client.RpcClient;
import com.lixiaodao.rpc.core.message.RpcResponse;

public interface RpcClientFactory {

	void startClient(int timeout);

	void stopClinet();
	
	public void receiveResponse(RpcResponse response) throws Exception;
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param connectiontimeout
	 * @param keepalive
	 */
	public RpcClient getClient(String host,int port) throws Exception;
	
	public void putResponse(int reqeustId,LinkedBlockingQueue<RpcResponse> queue);

	
	public void removeResponse(int requestId);
	
	void removeRpcClient(String  key);
}
