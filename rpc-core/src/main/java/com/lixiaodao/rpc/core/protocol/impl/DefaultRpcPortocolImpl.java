package com.lixiaodao.rpc.core.protocol.impl;

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
		} else { // 服务端发送结果使用
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
	}

	@Override
	public Object decode(RpcByteBuffer wrapper, Object errorObject, int ... originPosArray) throws Exception {
		final int originPos;
		if(originPosArray != null && originPosArray.length == 1){
			originPos = originPosArray[0];
		}else{
			originPos = wrapper.readerIndex();
		}
		
		if(wrapper.readableBytes() <2){
			// 因为已经读过数据，所以要重新设置读index
			wrapper.setReaderIndex(originPos);
			return errorObject;
		}
		byte version = wrapper.readByte();
		if(version == (byte) 1){
			byte type = wrapper.readByte();
			if(type == REQUEST){
				// 我觉得这里的组包，可以用更简单的，就是用一个字段来记录总长度，然后判断一次就可以了
				if(wrapper.readableBytes() < REQUEST_HEADER_LEN -2){
					wrapper.setReaderIndex(originPos);
					return errorObject;
				}
				
				int codecType = wrapper.readByte();
				wrapper.readByte();// 我觉得是预留字段
				wrapper.readByte();
				wrapper.readByte();
				
				int requestId = wrapper.readInt();
				
				int timeout = wrapper.readInt();
				int targetInstanceLen = wrapper.readInt();
				int methodNameLen = wrapper.readInt();
				int argsCount = wrapper.readInt();
				int argInfosLen = argsCount * 4 * 2;
				int expectedLenInfoLen = argInfosLen + targetInstanceLen
						+ methodNameLen;
				
				if (wrapper.readableBytes() < expectedLenInfoLen) {
					wrapper.setReaderIndex(originPos);
					return errorObject;
				}
				
				int expectedLen = 0;
				int[] argsTypeLen = new int[argsCount];
				for (int i = 0; i < argsCount; i++) {
					argsTypeLen[i] = wrapper.readInt();
					expectedLen += argsTypeLen[i];
				}
				int[] argsLen = new int[argsCount];
				for (int i = 0; i < argsCount; i++) {
					argsLen[i] = wrapper.readInt();
					expectedLen += argsLen[i];
				}
				byte[] targetInstanceByte = new byte[targetInstanceLen];
				wrapper.readBytes(targetInstanceByte);
				
				byte[] methodNameByte = new byte[methodNameLen];
				wrapper.readBytes(methodNameByte);
				
				if (wrapper.readableBytes() < expectedLen) {
					wrapper.setReaderIndex(originPos);
					return errorObject;
				}
				byte[][] argTypes = new byte[argsCount][];
				for (int i = 0; i < argsCount; i++) {
					byte[] argTypeByte = new byte[argsTypeLen[i]];
					wrapper.readBytes(argTypeByte);
					argTypes[i] = argTypeByte;
				}
				Object[] args = new Object[argsCount];
				for (int i = 0; i < argsCount; i++) {
					byte[] argByte = new byte[argsLen[i]];
					wrapper.readBytes(argByte);
					args[i] = argByte;
				}
				
				RpcRequest rpcRequest = new RpcRequest(
						targetInstanceByte, methodNameByte, argTypes, args,
						timeout, requestId, codecType, TYPE);
				
				int messageLen = RpcProtocols.HEADER_LEN + REQUEST_HEADER_LEN
						+ expectedLenInfoLen + expectedLen;
				rpcRequest.setMessageLen(messageLen);
				return rpcRequest;
			}else if(type == RESPONSE){
				if (wrapper.readableBytes() < RESPONSE_HEADER_LEN - 2) {
					wrapper.setReaderIndex(originPos);
					return errorObject;
				}
				int codecType = wrapper.readByte();
				wrapper.readByte();
				wrapper.readByte();
				wrapper.readByte();
				
				int requestId = wrapper.readInt();
				
				int classNameLen = wrapper.readInt();
				int bodyLen = wrapper.readInt();
				if (wrapper.readableBytes() < classNameLen + bodyLen) {
					wrapper.setReaderIndex(originPos);
					return errorObject;
				}

				byte[] classNameBytes = null;
				if (codecType == RpcCodecFactroy.PB_CODEC) {
					classNameBytes = new byte[classNameLen];
					wrapper.readBytes(classNameBytes);
				}
				byte[] bodyBytes = new byte[bodyLen];
				wrapper.readBytes(bodyBytes);
				
				RpcResponse responseWrapper = new RpcResponse(
						requestId, codecType, TYPE);
				responseWrapper.setResponse(bodyBytes);
				responseWrapper.setResponseClassName(classNameBytes);
				int messageLen = RpcProtocols.HEADER_LEN + RESPONSE_HEADER_LEN
						+ classNameLen + bodyLen;
				responseWrapper.setMessageLen(messageLen);
				return responseWrapper;
			}else{
				throw new UnsupportedOperationException("protocol type :"
						+type + " is not supported");
			}
		}else{
			throw new UnsupportedOperationException("protocol version:"+version+"  "
					+ "  is not supported" );
		}
	}
}
