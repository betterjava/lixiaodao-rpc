package com.lixiaodao.rpc.tcp.nett4.spring.config.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.lixiaodao.rpc.core.util.StringUtils;
import com.lixiaodao.rpc.server.filter.RpcFilter;
import com.lixiaodao.rpc.tcp.nett4.server.RpcTcpServer;

public class RpcService implements ApplicationContextAware, ApplicationListener {

	private String interfacename;// 接口名称 key

	private String ref;// 服务类bean value

	private ApplicationContext applicationContext;

	private String filterRef;// 拦截器类

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// 监听spring application 事件，注册 服务
		if (StringUtils.isNullOrEmpty(filterRef) || !(applicationContext.getBean(filterRef) instanceof RpcFilter)) {
			RpcTcpServer.getInstance().registerService(interfacename, applicationContext.getBean(ref), null);
		} else {
			RpcTcpServer.getInstance().registerService(interfacename, applicationContext.getBean(ref), null);
		}
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

	public void setInterfacename(String interfacename) {
		this.interfacename = interfacename;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setFilterRef(String filterRef) {
		this.filterRef = filterRef;
	}

}
