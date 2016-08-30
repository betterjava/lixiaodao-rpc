package com.lixiaodao.rpc.tcp.nett4.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RpcByteBuffer implements com.lixiaodao.rpc.core.bytebuffer.RpcByteBuffer {

	private ByteBuf buffer;
	private ChannelHandlerContext ctx;

	public RpcByteBuffer(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public RpcByteBuffer get(int capacity) {
		if (buffer != null) {
			return this;
		}
		buffer = ctx.alloc().buffer(capacity);
		return this;
	}

	@Override
	public void writeByte(int index, byte data) {
		buffer.writeByte(data);
	}

	@Override
	public void writeByte(byte data) {
		buffer.writeByte(data);
	}

	@Override
	public byte readByte() {
		return buffer.readByte();
	}

	@Override
	public void writeInt(int data) {
		buffer.writeInt(data);
	}

	@Override
	public void writeBytes(byte[] data) {
		buffer.writeBytes(data);
	}

	@Override
	public int readableBytes() {
		return buffer.readableBytes();
	}

	@Override
	public int readInt() {
		return buffer.readInt();
	}

	@Override
	public void readBytes(byte[] dst) {
		buffer.readBytes(dst);
	}

	@Override
	public int readerIndex() {
		return buffer.readerIndex();
	}

	@Override
	public void setReaderIndex(int readerIndex) {
		buffer.setIndex(readerIndex, buffer.writerIndex());
	}
	public static void main(String[] args) {
	}

}
