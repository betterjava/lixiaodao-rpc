package com.lixiaodao.rpc.core.server.handler.impl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lixiaodao.rpc.core.server.handler.RpcServerHandler;
import com.lixiaodao.rpc.server.filter.RpcFilter;

public class RpcTcpServerHandlerImpl implements RpcServerHandler {

	private static final Map<String, RpcFilterBean> services = new ConcurrentHashMap<>();
	private static final Map<String, Method> cacheMehods = new ConcurrentHashMap<>();

	@Override
	public void registrerService(String serviceName, Object instanceObject, RpcFilter rpcFilter) {
		RpcFilterBean rpcFilterBean = new RpcFilterBean(instanceObject, rpcFilter);
		services.put(serviceName, rpcFilterBean);

		Class<?> instanceClass = instanceObject.getClass();
		Method[] methods = instanceClass.getDeclaredMethods();

		if (methods != null && methods.length > 0) {
			for (Method method : methods) {
				Class<?>[] argTypes = method.getParameterTypes();
				StringBuilder methodKeyBuilder = new StringBuilder();
				methodKeyBuilder.append(serviceName).append("#");
				methodKeyBuilder.append(method.getName()).append("$");
				if (argTypes == null || argTypes.length <= 0) {
					continue;
				}
				for (Class<?> argClass : argTypes) {
					methodKeyBuilder.append(argClass.getName()).append("_");
				}
				cacheMehods.put(methodKeyBuilder.toString(), method);
			}
		}
	}

	@Override
	public void clear() {
		services.clear();
		cacheMehods.clear();
	}

	private class RpcFilterBean {
		private Object object;

		private RpcFilter rpcFilter;

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public RpcFilter getRpcFilter() {
			return rpcFilter;
		}

		public void setRpcFilter(RpcFilter rpcFilter) {
			this.rpcFilter = rpcFilter;
		}

		public RpcFilterBean(Object object, RpcFilter rpcFilter) {
			super();
			this.object = object;
			this.rpcFilter = rpcFilter;
		}

	}

}
