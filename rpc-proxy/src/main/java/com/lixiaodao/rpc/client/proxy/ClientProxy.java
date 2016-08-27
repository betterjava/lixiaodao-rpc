package com.lixiaodao.rpc.client.proxy;

public interface ClientProxy {

	/**
	 * 获取 客户端 调用 代理类，在这个类中发送真正的 请求 给服务端
	 * 
	 * @param clazz
	 *            类
	 * @param timeout
	 *            超时时间
	 * @param codecType
	 *            编码类型
	 * @param protocolType
	 *            协议类型
	 * @param targetInstanceName
	 *            实例名称
	 * @param group
	 *            组名
	 * @return
	 */
	<T> T getProxyService(Class<T> clazz, int timeout, int codecType, int protocolType, String targetInstanceName,
			String group);
}
