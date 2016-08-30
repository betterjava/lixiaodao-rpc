package com.lixiaodao.rpc.core.client;

import com.lixiaodao.rpc.core.client.factory.RpcClientFactory;

public interface RpcClient {
	/**
	 * 动态调用
	 * @param targetInstanceName
	 * @param methodName
	 * @param argTypes
	 * @param args
	 * @param timeout
	 * @param codecType
	 * @param protocolType
	 * @return
	 * @throws Exception
	 */
	public Object invokeImpl(String targetInstanceName, String methodName,
			String[] argTypes, Object[] args, int timeout, int codecType, int protocolType)
			throws Exception;
	
	/**
	 * Get factory
	 */
	public RpcClientFactory getClientFactory();
	
	String getServerIP();
	
	
	int getServerPort();
}
