package com.lixiaodao.rpc.core.client.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import com.lixiaodao.registry.client.RpcClientRegistry;
import com.lixiaodao.rpc.core.client.RpcClient;
import com.lixiaodao.rpc.core.client.factory.RpcClientFactory;

public abstract class AbstractRpcClientInvocationHandler implements InvocationHandler {

	private String group;
	private int timeout;
	private String targetInstanceName;
	private int codecType;
	private int protocolType;

	public AbstractRpcClientInvocationHandler(String group, int timeout, String targetInstanceName, int codecType,
			int protocolType) {
		super();
		this.group = group;
		this.timeout = timeout;
		this.targetInstanceName = targetInstanceName;
		this.codecType = codecType;
		this.protocolType = protocolType;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RpcClient client = null;

		String[] groups = group.split(",");

		Random r = new Random();

		int i = r.nextInt(groups.length);
		
		Set<InetSocketAddress>addresses =RpcClientRegistry.getInstance().getServerByGroup(groups[i]);
		
		int j=r.nextInt(addresses.size());
		
		InetSocketAddress addr = new ArrayList<>(addresses).get(j);
		
		client = getClientFactory().getClient(addr.getAddress().getHostAddress(), addr.getPort());
		
		String methodName = method.getName();
		String[] argTypes = createParamSignature(method.getParameterTypes());
		Object result = client.invokeImpl( targetInstanceName, methodName,
				argTypes, args, timeout,  codecType,  protocolType);
		return result;
	}
	
	private String[] createParamSignature(Class<?>[] argTypes) {
        if (argTypes == null || argTypes.length == 0) {
            return new String[] {};
        }
        String[] paramSig = new String[argTypes.length];
        for (int x = 0; x < argTypes.length; x++) {
            paramSig[x] = argTypes[x].getName();
        }
        return paramSig;
    }

	public abstract RpcClientFactory getClientFactory();

}
