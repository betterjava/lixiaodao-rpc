package com.lixiaodao.rpc.tcp.nett4.client.proxy;

import java.lang.reflect.Proxy;

import com.lixiaodao.rpc.client.proxy.ClientProxy;
import com.lixiaodao.rpc.tcp.nett4.client.invocation.RpcTcpClientInvocationHandler;

public class RpcTcpClientProxy implements ClientProxy {

	private RpcTcpClientProxy() {
	}

	private static class SinglentonHolder {
		static final RpcTcpClientProxy instance = new RpcTcpClientProxy();
	}

	public static RpcTcpClientProxy getInstance() {
		return SinglentonHolder.instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProxyService(Class<T> clazz, int timeout, int codecType, int protocolType,
			String targetInstanceName, String group) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new RpcTcpClientInvocationHandler(group, timeout,
				targetInstanceName, codecType, protocolType));
	}

}
