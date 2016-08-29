package com.lixiaodao.rpc.core.codec.factory;

import com.lixiaodao.rpc.core.codec.RpcDecoder;
import com.lixiaodao.rpc.core.codec.RpcEncoder;
import com.lixiaodao.rpc.core.codec.impl.HessianDecoder;
import com.lixiaodao.rpc.core.codec.impl.HessianEncoder;
import com.lixiaodao.rpc.core.codec.impl.JavaDecoder;
import com.lixiaodao.rpc.core.codec.impl.JavaEncoder;
import com.lixiaodao.rpc.core.codec.impl.KryoDecoder;
import com.lixiaodao.rpc.core.codec.impl.KryoEncoder;
import com.lixiaodao.rpc.core.codec.impl.ProtocolBufDecoder;
import com.lixiaodao.rpc.core.codec.impl.ProtocolBufEncoder;

public class RpcCodecFactroy {

	public static final int JAVA_CODEC = 1;

	public static final int HESSIAN_CODEC = 2;

	public static final int PB_CODEC = 3;

	public static final int KRYO_CODEC = 4;

	private static RpcEncoder[] encoders = new RpcEncoder[5];

	private static RpcDecoder[] decoders = new RpcDecoder[5];

	static {
		addEncoder(JAVA_CODEC, new JavaEncoder());
		addEncoder(HESSIAN_CODEC, new HessianEncoder());
		addEncoder(PB_CODEC, new ProtocolBufEncoder());
		addEncoder(KRYO_CODEC, new KryoEncoder());

		addDecoder(JAVA_CODEC, new JavaDecoder());
		addDecoder(HESSIAN_CODEC, new HessianDecoder());
		addDecoder(PB_CODEC, new ProtocolBufDecoder());
		addDecoder(KRYO_CODEC, new KryoDecoder());
	}

	public static void addEncoder(int encoderKey, RpcEncoder encoder) {
		encoders[encoderKey] = encoder;
	}

	public static void addDecoder(int encoderKey, RpcDecoder decoder) {
		decoders[encoderKey] = decoder;
	}
	
	public static RpcEncoder getEncoder(int encoderKey) {
		return encoders[encoderKey];
	}

	public static RpcDecoder getDecoder(int decoderKey) {
		return decoders[decoderKey];
	}
	
	
}
