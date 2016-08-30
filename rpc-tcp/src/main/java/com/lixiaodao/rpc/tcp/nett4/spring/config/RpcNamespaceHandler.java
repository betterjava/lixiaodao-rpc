package com.lixiaodao.rpc.tcp.nett4.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("application", new RpcApplicationParser());
		registerBeanDefinitionParser("service", new RpcServiceParser());
		registerBeanDefinitionParser("registry", new RpcRegistryPaser());
		registerBeanDefinitionParser("reference", new RpcReferenceParser());
	}

}
