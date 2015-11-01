package org.tangerine.protocol;

import org.tangerine.Constant.PacketType;

public class Packet {

	private PacketHead packetHead;
	
	private Object body;
	
	public Packet() {}
	
	public Packet(PacketHead packetHead, Object body) {
		super();
		this.packetHead = packetHead;
		this.body = body;
	}
	
	public Packet(Message body) {
		super();
		this.packetHead = new PacketHead();
		packetHead.setType(PacketType.PCK_DATA);
		this.body = body;
	}

	public PacketHead getPacketHead() {
		return packetHead;
	}

	public void setPacketHead(PacketHead packetHead) {
		this.packetHead = packetHead;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public static Packet buildHeartbeatPacket() {
		PacketHead packetHead = new PacketHead(PacketType.PCK_HEARTBEAT, (short) 0);
		return new Packet(packetHead, null);
	}
	
	public static Packet buildACKPacket() {
		PacketHead packetHead = new PacketHead(PacketType.PCK_SHAKE_ACK, (short) 0);
		return new Packet(packetHead, null);
	}
}
