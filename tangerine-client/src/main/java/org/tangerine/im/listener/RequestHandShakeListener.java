package org.tangerine.im.listener;

import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

import org.tangerine.Constant.Config;
import org.tangerine.Constant.PacketType;
import org.tangerine.HandShakeRequest;
import org.tangerine.im.IMConnection;
import org.tangerine.protocol.Packet;
import org.tangerine.protocol.PacketHead;
import org.tangerine.util.JsonUtil;

public class RequestHandShakeListener extends ConnectionListenerAdapter {

	/**
	 * 发送握手请求
	 * @throws Exception 
	 */
	public void connected(IMConnection connection) {
		Packet packet = new Packet();
		HandShakeRequest msg = new HandShakeRequest();
		msg.setVersion("1.0");
		
		byte[] data = null;
		try {
			data = JsonUtil.toJson(msg).getBytes(Config.DEFAULT_CHARTSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		packet.setPacketHead(new PacketHead(PacketType.PCK_HANDSHAKE, data.length));
		packet.setBody(Unpooled.wrappedBuffer(data));
		
		try {
			connection.getPacketWriter().send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
