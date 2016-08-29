package com.lixiaodao.rpc.core.message;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import com.lixiaodao.rpc.core.codec.factory.RpcCodecFactroy;

public class RpcRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -741506287602773888L;

	private byte[] targetInstanceName;

	private byte[] methodName;

	/**
	 * 参数类型的 的数组列表
	 */
	private byte[][] argTypes;

	private Object[] requestObjects = null;

	private Object message = null;

	private int timeout = 0;

	private int id;

	private int protocolType;

	private int codecType = RpcCodecFactroy.JAVA_CODEC;

	private int messageLen;
	private static final AtomicInteger requestIdSeq = new AtomicInteger();

	public RpcRequest() {

	}

	public RpcRequest(byte[] targetInstanceName, byte[] methodName, byte[][] argTypes, Object[] requestObjects,
			int timeout, int codecType, int protocolType) {
		this(targetInstanceName, methodName, argTypes, requestObjects, timeout, get(), codecType, protocolType);
	}

	public RpcRequest(byte[] targetInstanceName, byte[] methodName, byte[][] argTypes, Object[] requestObjects,
			int timeout, int id, int codecType, int protocolType) {
		this.requestObjects = requestObjects;
		this.id = id;
		this.timeout = timeout;
		this.targetInstanceName = targetInstanceName;
		this.methodName = methodName;
		this.argTypes = argTypes;
		this.codecType = codecType;
		this.protocolType = protocolType;

	}

	public static Integer get() {

		return requestIdSeq.incrementAndGet();
	}

	public byte[] getTargetInstanceName() {
		return targetInstanceName;
	}

	public byte[] getMethodName() {
		return methodName;
	}

	public byte[][] getArgTypes() {
		return argTypes;
	}

	public Object[] getRequestObjects() {
		return requestObjects;
	}

	public Object getMessage() {
		return message;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getId() {
		return id;
	}

	public int getProtocolType() {
		return protocolType;
	}

	public int getCodecType() {
		return codecType;
	}

	public int getMessageLen() {
		return messageLen;
	}
	
	

}
