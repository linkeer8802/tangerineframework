package org.tangerine.protocol;

import io.netty.buffer.Unpooled;

import java.util.Map;

import org.tangerine.common.json.JsonUtil;
import org.tangerine.protocol.model.AuthResponse;
import org.tangerine.protocol.model.HandShakeRequest;
import org.tangerine.protocol.model.HandShakeResponse;

/**
 * Tangerine协议工具类
 * @author weird
 *
 */
public class TanProtocol {

	/**
	 * 构建握手请求
	 * @param version 客户端的版本号
	 * @param extras  额外扩展信息
	 * @return Packet数据包
	 */
	public static Packet buildHandshakeRequest(String version, Map<String, Object> extras) {
		
		Packet packet = new Packet(Packet.Type.PCK_HANDSHAKE);
		
		HandShakeRequest request = new HandShakeRequest();
		request.setVersion(version);
		request.setExtras(extras);
		
		packet.setPayload(Unpooled.wrappedBuffer(JsonUtil.toJsonBytes(request)));
		packet.setLength(packet.getPayload().readableBytes());
		
		return packet;
	}
	/**
	 * 构建握手响应
	 * @param code 状态码
	 * @param dict route字段压缩的映射表
	 * @param extras 额外扩展信息
	 * @return Packet数据包
	 */
	public static Packet buildHandshakeResponse(int code, int heartbeat, Map<String, Object> dict, Map<String, Object> extras) {
		
		Packet packet = new Packet(Packet.Type.PCK_HANDSHAKE);
		
		HandShakeResponse response = new HandShakeResponse();
		response.setCode(code);
		response.setHeartbeat(heartbeat);
		response.setDict(dict);
		response.setExtras(extras);
		
		packet.setPayload(Unpooled.wrappedBuffer(JsonUtil.toJsonBytes(response)));
		packet.setLength(packet.getPayload().readableBytes());
		
		return packet;
	}
	/**
	 * 构建心跳包
	 * @return Packet数据包
	 */
	public static Packet buildHeartbeatPacket() {
		
		Packet packet = new Packet(Packet.Type.PCK_HEARTBEAT);
		packet.setLength(0);
		packet.setPayload(Unpooled.EMPTY_BUFFER);
		
		return packet;
	}
	
	/**
	 * 构建认证响应
	 * @param code 状态码
	 * @param dict route字段压缩的映射表
	 * @param extras 额外扩展信息
	 * @return Packet数据包
	 */
	public static Packet buildAuthResponse(int code, String msg, Map<String, Object> extras) {
		
		Packet packet = new Packet(Packet.Type.PCK_AUTH);
		
		AuthResponse response = new AuthResponse();
		response.setCode(code);
		response.setMsg(msg);
		response.getExtras().putAll(extras);
		
		packet.setPayload(Unpooled.wrappedBuffer(JsonUtil.toJsonBytes(response)));
		packet.setLength(packet.getPayload().readableBytes());
		
		return packet;
	}
}
