package com.lixiaodao.rpc.core.server;

import com.lixiaodao.rpc.server.filter.RpcFilter;

public interface RpcServer {

	void registerService(String serviceName,Object targetInstance,RpcFilter rpcFilter);

	void start(int port, int timeout) throws Exception;

	void stop() throws Exception;
}
