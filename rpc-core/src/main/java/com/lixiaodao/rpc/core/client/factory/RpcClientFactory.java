package com.lixiaodao.rpc.core.client.factory;

public interface RpcClientFactory {

	void startClient(int timeout);

	void stopClinet();
}
