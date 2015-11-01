package org.tangerine.im;

import io.netty.buffer.ByteBuf;

import org.tangerine.AuthResponse;
import org.tangerine.Constant.Config;
import org.tangerine.Constant.PacketType;
import org.tangerine.HandShakeResponse;
import org.tangerine.protocol.Packet;
import org.tangerine.util.JsonUtil;

public class PacketHandler {

	public void handle(IMConnection connection, Packet packet) throws Exception {
		
		byte type = packet.getPacketHead().getType();
		String body = new String(((ByteBuf)packet.getBody()).array(), Config.DEFAULT_CHARTSET);
		
		if (type == PacketType.PCK_HANDSHAKE) {
			handleHandshake(connection, JsonUtil.fromJson(body, HandShakeResponse.class));
			
		} else if (type == PacketType.PCK_SHAKE_ACK) {
			handleShakeACK(connection);
			
		} else if (type == PacketType.PCK_HEARTBEAT) {
			handleHeartbeat(connection);
		} else if (type == PacketType.PCK_AUTH) {
			handleAuth(connection, JsonUtil.fromJson(body, AuthResponse.class));
		}
	}

	private void handleAuth(IMConnection connection, AuthResponse response) {
		System.out.println("login response:" + response);
	}

	private void handleHeartbeat(final IMConnection connection) throws Exception {
//		System.out.println("client: recive Heartbeat packet at " + new Date());
		//设置最后一次心跳时间
		connection.setHeartbeatLastTime(System.currentTimeMillis());
	}

	private void handleShakeACK(IMConnection connection) {
		
	}

	private void handleHandshake(IMConnection connection, HandShakeResponse handShakeResponse) {
		synchronized (connection) {
			if (handShakeResponse.getCode() == 500) {
				System.out.println("handShake error.");
				
			} else if (handShakeResponse.getCode() == 501) {
				System.out.println("client version error.");
				
			} else {
				//发送ack
				try {
					connection.getPacketWriter().send(Packet.buildACKPacket());
					
					//FIXME 握手成功后发送第一个心跳包
					connection.setHeartbeatLastTime(System.currentTimeMillis());
					connection.getIdleStateHandler().initialize(connection,
												handShakeResponse.getHeartbeat()*2,
												handShakeResponse.getHeartbeat(), 0);
//					connection.getPacketWriter().send(Packet.buildHeartbeatPacket());
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					connection.notifyAll();
				}
				
			}
		}
	}
}
