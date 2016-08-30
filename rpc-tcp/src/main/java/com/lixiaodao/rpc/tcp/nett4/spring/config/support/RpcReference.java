package com.lixiaodao.rpc.tcp.nett4.spring.config.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.lixiaodao.rpc.core.util.StringUtils;
import com.lixiaodao.rpc.tcp.nett4.client.proxy.RpcTcpClientProxy;

/**
 * 实现factorybean 是为了构造代理对象
 * 
 * @author lijia
 *
 */
public class RpcReference implements FactoryBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcReference.class);

	/**
	 * 接口名称
	 */
	private String interfacename;

	/**
	 * 超时时间
	 */
	private int timeout;

	/**
	 * 编码类型
	 */
	private int codecType;
	/**
	 * 协议类型
	 */
	private int protocolType;

	/**
	 * 组名
	 */
	private String group;

	@Override
	public Object getObject() throws Exception {
		return RpcTcpClientProxy.getInstance().getProxyService(getObjectType(), timeout, codecType, protocolType, getObjectType().getName(), group);
	}

	@Override
	public Class<?> getObjectType() {
		try {
			if (StringUtils.isNullOrEmpty(interfacename)) {
				LOGGER.warn("interfacename is null");
				return null;
			} else {
				return Thread.currentThread().getContextClassLoader().loadClass(interfacename);
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error("spring 解析失败", e);
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getInterfacename() {
		return interfacename;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getCodecType() {
		return codecType;
	}

	public int getProtocolType() {
		return protocolType;
	}

	public String getGroup() {
		return group;
	}

	public void setInterfacename(String interfacename) {
		this.interfacename = interfacename;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	public void setProtocolType(int protocolType) {
		this.protocolType = protocolType;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	
	
}
