package org.tangerine.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class PacketHead {

	private Byte type;
	
	private Integer length;
	
	public PacketHead() {}

	public PacketHead(byte type, int length) {
		this.type = type;
		this.length = length;
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
	/**
	 * type + length
	 * @return
	 */
	public static int getHeadLength() {
		return 4;
	}
	
	public static PacketHead decode(ByteBuf buf) {
		
		PacketHead packetHead = new PacketHead();
		packetHead.setType(buf.readByte());
		packetHead.setLength(buf.readMedium());
		
		return packetHead;
	}
	
	public static ByteBuf encode(PacketHead packetHead) {
		ByteBuf out = Unpooled.buffer(getHeadLength());
		out.writeByte(packetHead.getType());
		out.writeMedium(packetHead.getLength());
		
		return out;
	}
}
