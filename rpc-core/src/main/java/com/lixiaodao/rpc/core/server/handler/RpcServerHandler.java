package com.lixiaodao.rpc.core.server.handler;

import com.lixiaodao.rpc.core.message.RpcRequest;
import com.lixiaodao.rpc.core.message.RpcResponse;
import com.lixiaodao.rpc.server.filter.RpcFilter;

public interface RpcServerHandler {
	
	/**
	 * 注册服务（key--真正要调用的类）
	 * @param serviceName
	 * @param instanceObject
	 * @param rpcFilter
	 */
	void registrerService(String serviceName, Object instanceObject, RpcFilter rpcFilter);

	/**
	 * 清楚 
	 */
	void clear();
	
	/**
	 * 处理真正的请求
	 * @param request
	 * @param codecType
	 * @param procotolType
	 * @return
	 */
	RpcResponse handleRequest(RpcRequest request,int codecType,int procotolType );
}
