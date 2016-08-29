/**
 * 
 */
package com.lixiaodao.rpc.core.protocol.factory;

import com.lixiaodao.rpc.core.protocol.IRpcProtocol;
import com.lixiaodao.rpc.core.protocol.impl.DefaultRpcPortocolImpl;

public class RpcProtocolFactory {
	
	private static IRpcProtocol[] protocolHandlers = new IRpcProtocol[2];
	
	
	static{
		registerProtocol(DefaultRpcPortocolImpl.TYPE, new DefaultRpcPortocolImpl());
	}
	
	private static void registerProtocol(int type,IRpcProtocol customProtocol){
		if(type > protocolHandlers.length){
			IRpcProtocol[] newProtocolHandlers = new IRpcProtocol[type + 1];
			System.arraycopy(protocolHandlers, 0, newProtocolHandlers, 0, protocolHandlers.length);
			protocolHandlers = newProtocolHandlers;
			
		}
		protocolHandlers[type] = customProtocol;
	}
	
	public static IRpcProtocol getProtocol(int type){
		return protocolHandlers[type];
	}

}
