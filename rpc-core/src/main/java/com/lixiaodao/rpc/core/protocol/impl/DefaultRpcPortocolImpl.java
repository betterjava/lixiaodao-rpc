package com.lixiaodao.rpc.core.protocol.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lixiaodao.rpc.core.bytebuffer.RpcByteBuffer;
import com.lixiaodao.rpc.core.codec.factory.RpcCodecFactroy;
import com.lixiaodao.rpc.core.message.RpcRequest;
import com.lixiaodao.rpc.core.message.RpcResponse;
import com.lixiaodao.rpc.core.protocol.IRpcProtocol;
import com.lixiaodao.rpc.core.protocol.RpcProtocols;

public class DefaultRpcPortocolImpl implements IRpcProtocol {

	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultRpcPortocolImpl.class);

	public static final int TYPE = 1;
	
	private static final int REQUEST_HEADER_LEN = 1 * 6 + 5 * 4 ;

	private static final int RESPONSE_HEADER_LEN = 1 * 6 + 3 * 4 ;
	
	private static final byte VERSION = (byte) 1;

	private static final byte REQUEST = (byte) 0;

	private static final byte RESPONSE = (byte) 1;

	@Override
	public RpcByteBuffer encode(Object message, RpcByteBuffer bytebufferWrapper) throws Exception {
		if (!(message instanceof RpcRequest) && !(message instanceof RpcResponse)) {
			throw new Exception("only support RpcRequest && RpcResponse");
		}

		if (message instanceof RpcRequest) { // 客户端发送数据
			try {
				
				RpcRequest wrapper = (RpcRequest) message;
				/**
				 * 1.计算长度，分配buffer 空间
				 * 2.将字段逐个写入
				 */
				// 参数类型 字节总长度
				int requestArgTypeLen = 0;
				// 参数字节总长度
				int requestArgLen = 0;
				
				List<byte[]> requestArgTypes = new ArrayList<byte[]>();
				List<byte[]> requestArgs = new ArrayList<byte[]>();
				
				for(byte[] requestArgType :requestArgTypes){
					requestArgTypes.add(requestArgType);
					requestArgTypeLen += requestArgType.length;
				}
				
				Object[] requestObjects = wrapper.getRequestObjects();
				if (requestObjects != null) {
					for (Object requestArg : requestObjects) {
						byte[] requestArybyte = RpcCodecFactroy.getEncoder(wrapper.getCodecType()).encode(requestArg);
						requestArgs.add(requestArybyte);
						requestArgLen += requestArybyte.length;
					}
				}
				
				byte[] targetInstanceNameByte = wrapper.getTargetInstanceName();
				byte[] methodNameByte = wrapper.getMethodName();
				int id = wrapper.getId();
				byte type = REQUEST;
				int timeout = wrapper.getTimeout();
				
				// 接口名 字节长度
				int targetInstanceNameLen = targetInstanceNameByte.length;
				// 方法名子杰长度
				int methodNameLen = methodNameByte.length;
				
				int capactiy = RpcProtocols.HEADER_LEN      //  版本号 + type
							+REQUEST_HEADER_LEN				
							+requestArgs.size() * 4 * 2     // 每个参数的长度，和参数类型的长度都要写如，每个是一个int （4字节） ,然后*2
							+ targetInstanceNameLen
							+ methodNameLen
							+ requestArgTypeLen 			// 参数类型
						    + requestArgLen;				// 参数
				
				RpcByteBuffer buffer = bytebufferWrapper.get(capactiy);
				
				/**
				 * 开始写入数据
				 */
				//--------------HEADER_LEN----------------
				buffer.writeByte(RpcProtocols.CURRENT_VERSION);
				buffer.writeByte((byte) TYPE);
				
				//--------------REQUEST_HEADER_LEN----------------
				buffer.writeByte(VERSION);//1B
				buffer.writeByte(type);//1B
				buffer.writeByte((byte) wrapper.getCodecType());//1B
				buffer.writeByte((byte) 0);//1B
				buffer.writeByte((byte) 0);//1B
				buffer.writeByte((byte) 0);//1B
				
				buffer.writeInt(id);
				buffer.writeInt(timeout);//4B
				buffer.writeInt(targetInstanceNameByte.length);//4B

				buffer.writeInt(methodNameByte.length);//4B
				
				buffer.writeInt(requestArgs.size());//4B
				
				//------------------参数 长度+参数类型长度---------
				for(byte[] requestArgType :requestArgTypes){
					buffer.writeInt(requestArgType.length);
				}
				for(byte[] requestArg: requestArgs){
					buffer.writeInt(requestArg.length);
				}
				//------------------写入数据-----------------
				
				buffer.writeBytes(targetInstanceNameByte);

				buffer.writeBytes(methodNameByte);
				
				for (byte[] requestArgType : requestArgTypes) {
					buffer.writeBytes(requestArgType);
				}
				for (byte[] requestArg : requestArgs) {
					buffer.writeBytes(requestArg);
				}
				return buffer;
			} catch (Exception e) {
				LOGGER.error("encode request object error", e);
				throw e;
			}
		} else if(message instanceof RpcResponse){ // 服务端发送结果使用
			RpcResponse wrapper = (RpcResponse) message;
			byte[] body = new byte[0];
			byte[] className = new byte[0];
			int id = 0;
			try {
				// no return object
				if (wrapper.getResponse() != null) {
					className = wrapper.getResponse().getClass().getName()
							.getBytes();
					body = RpcCodecFactroy.getEncoder(wrapper.getCodecType())
							.encode(wrapper.getResponse());
				}
				if (wrapper.isError()) {
					className = wrapper.getException().getClass().getName()
							.getBytes();
					body = RpcCodecFactroy.getEncoder(wrapper.getCodecType())
							.encode(wrapper.getException());
				}
				id = wrapper.getRequestId();
			} catch (Exception e) {
				LOGGER.error("encode response object error", e);
				// still create responsewrapper,so client can get exception
				wrapper.setResponse(new Exception(
						"serialize response object error", e));
				className = Exception.class.getName().getBytes();
				body = RpcCodecFactroy.getEncoder(wrapper.getCodecType())
						.encode(wrapper.getResponse());
			}
			byte type = RESPONSE;
			int capacity = RpcProtocols.HEADER_LEN + RESPONSE_HEADER_LEN
					+ body.length;
			if (wrapper.getCodecType() == RpcCodecFactroy.PB_CODEC) {
				// pb 特殊之处，在于需要写入类名才能序列化
				capacity += className.length;
			}
			RpcByteBuffer byteBuffer = bytebufferWrapper.get(capacity);
			byteBuffer.writeByte(RpcProtocols.CURRENT_VERSION);
			byteBuffer.writeByte((byte) TYPE);
			byteBuffer.writeByte(VERSION);
			byteBuffer.writeByte(type);
			byteBuffer.writeByte((byte) wrapper.getCodecType());
			byteBuffer.writeByte((byte) 0);
			byteBuffer.writeByte((byte) 0);
			byteBuffer.writeByte((byte) 0);
			byteBuffer.writeInt(id);
			if (wrapper.getCodecType() == RpcCodecFactroy.PB_CODEC) {
				byteBuffer.writeInt(className.length);
			} else {
				byteBuffer.writeInt(0);
			}
			byteBuffer.writeInt(body.length);
			if (wrapper.getCodecType() == RpcCodecFactroy.PB_CODEC) {
				byteBuffer.writeBytes(className);
			}
			byteBuffer.writeBytes(body);
			return byteBuffer;
		}

		return null;
	}

	@Override
	public Object decode(RpcByteBuffer wrapper, Object errorObject, int ... originPos) throws Exception {
		
		return null;
	}

	public static void main(String[] args) {
	}
}
