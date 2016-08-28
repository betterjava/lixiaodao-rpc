package com.lixiaodao.rpc.tcp.nett4.spring.config.support;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.InitializingBean;

import com.lixiaodao.registry.server.RpcServerRegistry;
import com.lixiaodao.rpc.core.util.StringUtils;

public class RpcRegistry implements InitializingBean {
	private String ip;// 暴露的ip

	private int port;// 端口号

	private int timeout;

	private int procotolType;// 协议名称

	private int codecType;// 编码类型

	private String group;// 组

	private int threadCount;// 线程数

	@Override
	public void afterPropertiesSet() throws Exception {
		// 注册group 和 serverip 到zk
		RpcServerRegistry.getInstance().registerServer(group, getLocalhost());
		// TODO 启动serve
	}
	
	private String getLocalhost(){
		try {
			String ip = StringUtils.isNullOrEmpty(this.getIp()) ? InetAddress.getLocalHost().getHostAddress() : this.getIp();
			return ip+":"+port;
		} catch (UnknownHostException e) {
			throw new RuntimeException("无法获取本地Ip",e);
		}
		
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getProcotolType() {
		return procotolType;
	}

	public int getCodecType() {
		return codecType;
	}

	public String getGroup() {
		return group;
	}

	public int getThreadCount() {
		return threadCount;
	}

}
