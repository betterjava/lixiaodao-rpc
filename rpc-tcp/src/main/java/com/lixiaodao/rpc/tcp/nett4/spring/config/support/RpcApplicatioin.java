package com.lixiaodao.rpc.tcp.nett4.spring.config.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.lixiaodao.registry.client.RpcClientRegistry;
import com.lixiaodao.registry.server.RpcServerRegistry;
import com.lixiaodao.rpc.core.util.StringUtils;
import com.lixiaodao.rpc.tcp.nett4.client.factory.RpcTcpClientFactory;

public class RpcApplicatioin implements InitializingBean, DisposableBean {

	private String address;

	private int flag;

	private int timeout;

	@Override
	public void afterPropertiesSet() throws Exception {

		// 校验参数
		if (StringUtils.isNullOrEmpty(address)) {
			throw new RuntimeException("address   can not be null or empty");
		}
		if (StringUtils.isNullOrEmpty(flag)) {
			throw new RuntimeException("flag   can not be null or empty");
		}
		if (flag != 1 && flag != 2) {
			throw new RuntimeException("flag only be 1 or 2");
		}

		if (flag == 1) { // 服务端
			// 服务端连接zk
			RpcServerRegistry.getInstance().connectZookeeper(address, timeout);
		} else if (flag == 2) {// 客户端
			// 客户端连接zk
			RpcClientRegistry.getInstance().connectZookeeper(address, timeout);
			// 客户端启动，连接服务端 ，（其实这个时候还么有真正的连接）
			RpcTcpClientFactory.getInstance().startClient(timeout);
		}
	}

	@Override
	public void destroy() throws Exception {

		if (flag == 1) { // 服务端
			// 断开 zk
			RpcServerRegistry.getInstance().close();
		} else if (flag == 2) {// 客户端
			// 断开zk
			RpcClientRegistry.getInstance().close();
			//  停掉client
			RpcTcpClientFactory.getInstance().stopClinet();
		}

	}

	public String getAddress() {
		return address;
	}

	public int getFlag() {
		return flag;
	}

	public int getTimeout() {
		return timeout;
	}

}
