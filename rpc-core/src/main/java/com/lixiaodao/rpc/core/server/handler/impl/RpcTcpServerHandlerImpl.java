package com.lixiaodao.rpc.core.server.handler.impl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lixiaodao.rpc.core.codec.factory.RpcCodecFactroy;
import com.lixiaodao.rpc.core.message.RpcRequest;
import com.lixiaodao.rpc.core.message.RpcResponse;
import com.lixiaodao.rpc.core.server.handler.RpcServerHandler;
import com.lixiaodao.rpc.server.filter.RpcFilter;

public class RpcTcpServerHandlerImpl implements RpcServerHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpServerHandlerImpl.class);

	private static final Map<String, RpcFilterBean> services = new ConcurrentHashMap<>();
	// 有参数的方法都放在这里
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


		public RpcFilter getRpcFilter() {
			return rpcFilter;
		}

		public RpcFilterBean(Object object, RpcFilter rpcFilter) {
			super();
			this.object = object;
			this.rpcFilter = rpcFilter;
		}

	}

	@Override
	public RpcResponse handleRequest(RpcRequest request, int codecType, int procotolType) {
		
		RpcResponse response = new RpcResponse(request.getId(), codecType, procotolType);
		// 获取其他参数
		String targetInstanceName = new String (request.getMethodName());
		String methodName = new String(request.getMethodName());
		byte[][] argTypeBytes = request.getArgTypes();
		String[] argTypes = new String[argTypeBytes.length];
		for (int i = 0; i < argTypes.length; i++) {
			argTypes[i] =  new String(argTypeBytes[i]);
		}
		
		Object[] requestObjects = null; // 需要解码才能使用
		Method method = null;
		try {
			RpcFilterBean rpcFilterBean = services.get(targetInstanceName);
			if(rpcFilterBean == null){
				throw new Exception("no "+targetInstanceName+" instance exists on the server");
			}
			if(argTypes != null && argTypes.length>0){
				StringBuilder methodKey = new StringBuilder();
				methodKey.append(targetInstanceName).append("#");
				methodKey.append(methodName).append("$");
				//Class<?>[] argTypeClasses = new Class<?>[argTypes.length];
				for (int i = 0; i < argTypes.length; i++) {
					methodKey.append(argTypes[i]).append("_");
				}
				method = cacheMehods.get(methodKey.toString());
				if(method == null){
					throw new Exception("no method: "+methodKey.toString()+" find in "+targetInstanceName+" on the server");
				}
				
				requestObjects = new Object[argTypes.length];
				Object[] tmprequestObjects = request.getRequestObjects();
				for (int i = 0; i < tmprequestObjects.length; i++) {
					try{
						requestObjects[i] = RpcCodecFactroy.getDecoder(request.getCodecType()).decode(argTypes[i],(byte[])tmprequestObjects[i]);
					}
					catch(Exception e){
						throw new Exception("decode request object args error",e);
					}
				}
				
			}else{ // 没有参数
				method = rpcFilterBean.getObject().getClass().getMethod(methodName, new Class<?>[]{});
				if(method == null){
					throw new Exception("no method: "+methodName+" find in "+targetInstanceName+" on the server");
				}
				requestObjects = new Object[] {};
			}
			method.setAccessible(true);
			if(rpcFilterBean.getRpcFilter() != null){
				RpcFilter filter = rpcFilterBean.getRpcFilter();
				if(filter.doBeforeRequest(method, rpcFilterBean.getObject(), requestObjects)){
					response.setResponse(method.invoke(rpcFilterBean.getObject(), requestObjects));
					filter.doAfterRequest(response.getResponse());
				}else{
					response.setException(new Exception("无效的请求，服务端已经拒绝回应"));
				}
			}else{
				response.setResponse(method.invoke(rpcFilterBean.getObject(), requestObjects));
			}

		} catch (Exception e) {
			LOGGER.error("server handle request error",e);
			response.setException(e);
		}
		return response;
	}

}
