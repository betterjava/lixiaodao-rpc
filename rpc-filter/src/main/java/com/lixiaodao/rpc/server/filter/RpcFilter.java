package com.lixiaodao.rpc.server.filter;

import java.lang.reflect.Method;

public interface RpcFilter {

	/**
	 * 前置拦截
	 * 
	 * @param method
	 *            方法
	 * @param processor
	 *            实体
	 * @param requestObjects
	 *            请求参数列表 ？
	 * @return
	 */
	boolean doBeforeRequest(Method method, Object processor, Object[] requestObjects);

	/**
	 * 后置拦截
	 * 
	 * @param processor
	 *            响应
	 * @return
	 */
	boolean doAfterRequest(Object processor);
}
