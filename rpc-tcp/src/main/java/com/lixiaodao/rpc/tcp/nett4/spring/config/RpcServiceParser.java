package com.lixiaodao.rpc.tcp.nett4.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.lixiaodao.rpc.tcp.nett4.spring.config.support.RpcService;

public class RpcServiceParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String interfacename = element.getAttribute("interfacename");
		String ref=element.getAttribute("ref");
		String filterRef=element.getAttribute("filterRef");
		
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(RpcService.class);
		beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue("interfacename", interfacename);
        beanDefinition.getPropertyValues().addPropertyValue("ref", ref);
        beanDefinition.getPropertyValues().addPropertyValue("filterRef", filterRef);
        
        parserContext.getRegistry().registerBeanDefinition(interfacename, beanDefinition);
		return beanDefinition;
	}
}
