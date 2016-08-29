package com.lixiaodao.rpc.core.message;

import java.io.Serializable;

import com.lixiaodao.rpc.core.codec.factory.RpcCodecFactroy;

public class RpcResponse implements Serializable {

	/**
		 * 
		 */
	private static final long serialVersionUID = -7203398740717911134L;

	private int requestId;

	private Object response = null;

	private boolean isError = false;

	private Throwable exception = null;

	private int codecType = RpcCodecFactroy.JAVA_CODEC;

	private int protocolType;

	private int messageLen;

	private byte[] responseClassName;

	public RpcResponse(int requestId, int codecType, int protocolType) {
		this.requestId = requestId;
		this.codecType = codecType;
		this.protocolType = protocolType;
	}

	public int getRequestId() {
		return requestId;
	}

	public Object getResponse() {
		return response;
	}

	public boolean isError() {
		return isError;
	}

	public Throwable getException() {
		return exception;
	}

	public int getCodecType() {
		return codecType;
	}

	public int getProtocolType() {
		return protocolType;
	}

	public int getMessageLen() {
		return messageLen;
	}

	public byte[] getResponseClassName() {
		return responseClassName;
	}
	
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
}
