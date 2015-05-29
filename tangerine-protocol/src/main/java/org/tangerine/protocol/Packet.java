package org.tangerine.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 面向连接的数据包
 * @author weird
 *
 */
public class Packet {
	/**Packet 包头的长度**/
	public static final int LEN_PCK_HEAD = 4;
	
	public static class Type {
		
		/**客户端到服务器的握手请求以及服务器到客户端的握手响应**/
		public static final byte PCK_HANDSHAKE = 0x1;
		
		/** 客户端到服务器的握手ack */
		public static final byte PCK_SHAKE_ACK = 0x2;
		
		/***心跳包 **/
		public static final byte PCK_HEARTBEAT = 0x3;
		
		/**数据包**/
		public static final byte PCK_DATA = 0x4;
		
		/**服务器主动断开连接通知**/
		public static final byte PCK_DISCONNECT = 0x5;
		
		/**认证数据包**/
		public static final byte PCK_AUTH = 0x6;
	}
	
	/**package类型**/
	private Byte type;
	
	/**package payload数据长度**/
	private Integer length;
	
	/**传输的二进制内容**/
	private ByteBuf payload;
	
	public Packet(Byte type) {
		this.type = type;
	}
	
	public Packet() {}

	public Packet(Byte type, Integer length, ByteBuf payload) {
		super();
		this.type = type;
		this.length = length;
		this.payload = payload;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public ByteBuf getPayload() {
		return payload;
	}

	public void setPayload(ByteBuf payload) {
		this.payload = payload;
	}
	
	public static ByteBuf encode(Packet packet) {
		
		ByteBuf hBuffer = Unpooled.buffer();
		hBuffer.writeByte(packet.getType());
		hBuffer.writeMedium(packet.getLength());
		
		return Unpooled.wrappedBuffer(hBuffer, packet.getPayload());
	}
	
	public static Packet decode(ByteBuf buffer) {
		
		if (buffer.readableBytes() < LEN_PCK_HEAD) {
			return null;
		}
		
		byte type = buffer.readByte();
		int len = buffer.readMedium();
		
		if (buffer.readableBytes() < len) {
			return null;
		}
		
		Packet packet = new Packet();
		packet.setType(type);
		packet.setLength(len);
		ByteBuf payload = Unpooled.buffer(packet.getLength());
		buffer.readBytes(payload, 0, packet.getLength());
		payload.setIndex(0, packet.getLength());
		packet.setPayload(payload);
		
//		packet.setPayload(buffer.readSlice(packet.getLength()).retain());
		
		return packet;
	}
}


