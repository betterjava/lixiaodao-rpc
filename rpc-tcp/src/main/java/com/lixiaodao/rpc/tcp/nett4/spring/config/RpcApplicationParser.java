package com.lixiaodao.rpc.tcp.nett4.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.lixiaodao.rpc.tcp.nett4.spring.config.support.RpcApplicatioin;

public class RpcApplicationParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {

		String id = element.getAttribute("id");
		String address = element.getAttribute("address");
		String flag = element.getAttribute("flag");
		String timeout = element.getAttribute("timeout");

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(RpcApplicatioin.class);
		beanDefinition.setLazyInit(false);
		
		beanDefinition.getPropertyValues().addPropertyValue("address", address);
		beanDefinition.getPropertyValues().addPropertyValue("flag", Integer.parseInt(flag));
		beanDefinition.getPropertyValues().addPropertyValue("timeout", Integer.parseInt(timeout));

		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}

}
