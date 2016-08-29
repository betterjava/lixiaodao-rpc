package com.lixiaodao.rpc.tcp.nett4.client.proxy;

import com.lixiaodao.rpc.client.proxy.ClientProxy;

public class RpcTcpClientProxy implements ClientProxy {

	private RpcTcpClientProxy() {
	}

	private static class SinglentonHolder {
		static final RpcTcpClientProxy instance = new RpcTcpClientProxy();
	}

	public static RpcTcpClientProxy getInstance() {
		return SinglentonHolder.instance;
	}

	@Override
	public <T> T getProxyService(Class<T> clazz, int timeout, int codecType, int protocolType,
			String targetInstanceName, String group) {
		// TODO Auto-generated method stub
		return null;
	}

}
