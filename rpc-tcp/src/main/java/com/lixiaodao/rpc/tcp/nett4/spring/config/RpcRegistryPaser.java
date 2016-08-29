package com.lixiaodao.rpc.tcp.nett4.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class RpcRegistryPaser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// <commonrpc:registry id="rpcRegistry" port="10010" timeout="200"
		// procotolType="1" codecType="4" group="demo"
		// threadCount="200"></commonrpc:registry>
		String id = element.getAttribute("id");
		String port = element.getAttribute("port");
		String procotolType = element.getAttribute("procotolType");
		String codecType = element.getAttribute("codecType");
		String group = element.getAttribute("group");
		String threadCount = element.getAttribute("threadCount");

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.getPropertyValues().addPropertyValue("port", Integer.parseInt(port));
		beanDefinition.getPropertyValues().addPropertyValue("procotolType", Integer.parseInt(procotolType));
		beanDefinition.getPropertyValues().addPropertyValue("codecType", Integer.parseInt(codecType));
		beanDefinition.getPropertyValues().addPropertyValue("group", Integer.parseInt(group));
		beanDefinition.getPropertyValues().addPropertyValue("threadCount", Integer.parseInt(threadCount));

		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}

}
