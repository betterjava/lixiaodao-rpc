package com.lixiaodao.rpc.core.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lixiaodao.rpc.core.codec.factory.RpcCodecFactroy;
import com.lixiaodao.rpc.core.message.RpcRequest;
import com.lixiaodao.rpc.core.message.RpcResponse;
import com.lixiaodao.rpc.core.util.StringUtils;

public abstract class AbstractRpcClient implements RpcClient {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractRpcClient.class);
	
	@Override
	public Object invokeImpl(String targetInstanceName, String methodName, String[] argTypes, Object[] args,
			int timeout, int codecType, int protocolType) throws Exception {
		
		byte[][] argTypeBytes = new byte[argTypes.length][];
		for(int i =0; i < argTypes.length; i++) {
		    argTypeBytes[i] =  argTypes[i].getBytes();
		}
		RpcRequest wrapper = new RpcRequest(targetInstanceName.getBytes(),
				methodName.getBytes(), argTypeBytes, args, timeout, codecType, protocolType);
		return invodeIntinal(wrapper);
	}

	private Object invodeIntinal(RpcRequest resquest) throws Exception {

		long beginTime = System.currentTimeMillis();

		LinkedBlockingQueue<RpcResponse> queuqe = new LinkedBlockingQueue<>(1);

		getClientFactory().putResponse(resquest.getId(), queuqe);

		RpcResponse response = null;

		try {
			sendRequest(resquest);
		} catch (Exception e) {
			LOGGER.error("send request to os sendbuffer error", e);
			throw new RuntimeException("send request to os sendbuffer error", e);
		}

		Object result = null;
		try {

			result = queuqe.poll(resquest.getTimeout() - (System.currentTimeMillis() - beginTime),
					TimeUnit.MILLISECONDS);
		} finally {
			getClientFactory().removeResponse(resquest.getId());
		}
		
		if(result==null&&(System.currentTimeMillis() - beginTime)<=resquest.getTimeout()){//返回结果集为null
			response=new RpcResponse(resquest.getId(), resquest.getCodecType(), resquest.getProtocolType());
		}else if(result==null&&(System.currentTimeMillis() - beginTime)>resquest.getTimeout()){//结果集超时
			String errorMsg = "receive response timeout("
					+ resquest.getTimeout() + " ms),server is: "
					+ getServerIP() + ":" + getServerPort()
					+ " request id is:" + resquest.getId();
			LOGGER.error(errorMsg);
			response=new RpcResponse(resquest.getId(), resquest.getCodecType(), resquest.getProtocolType());
			response.setException( new Throwable(errorMsg));
		}else if(result!=null){
			response = (RpcResponse) result;
		}
		
		try{
			if (response.getResponse() instanceof byte[]) {
				String responseClassName = null;
				if(response.getResponseClassName() != null){
					responseClassName = new String(response.getResponseClassName());
				}
				if(((byte[])response.getResponse()).length == 0){
					response.setResponse(null);
				}else{
					Object responseObject = RpcCodecFactroy.getDecoder(response.getCodecType()).decode(
						responseClassName,(byte[]) response.getResponse());
					if (responseObject instanceof Throwable) {
						response.setException((Throwable) responseObject);
					} 
					else {
						response.setResponse(responseObject);
					}
				}
			}
		}catch(Exception e){
			LOGGER.error("Deserialize response object error", e);
			throw new Exception("Deserialize response object error", e);
		}
		
		if (!StringUtils.isNullOrEmpty(response.getException())) {
			Throwable t = response.getException();
			//t.fillInStackTrace();
			String errorMsg = "server error,server is: " + getServerIP()
					+ ":" + getServerPort() + " request id is:"
					+ resquest.getId();
			LOGGER.error(errorMsg, t);
			//destroy();
			//throw new Exception(errorMsg, t);
			return null;
		}

		return response.getResponse();
	}
	
	
	public abstract void sendRequest(RpcRequest rpcRequest) throws Exception; 
}
