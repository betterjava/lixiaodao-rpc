package com.lixiaodao.rpc.tcp.nett4.spring.config.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class RpcService implements ApplicationContextAware, ApplicationListener {

	private String interfacename;// 接口名称 key

	private String ref;// 服务类bean value

	private ApplicationContext applicationContext;

	private String filterRef;// 拦截器类

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		//TODO 监听spring application 事件，注册 服务
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// 获取 spring 上下文
		this.applicationContext = applicationContext;
	}

	public String getInterfacename() {
		return interfacename;
	}

	public String getRef() {
		return ref;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getFilterRef() {
		return filterRef;
	}

}
