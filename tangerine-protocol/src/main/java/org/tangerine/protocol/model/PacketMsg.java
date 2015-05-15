package org.tangerine.protocol.model;

import io.netty.buffer.ByteBuf;

/**
 * 用户消息
 * @author weird
 *
 */
public class PacketMsg {
	/**消息Id**/
	private Integer messageId;
	/**消息路由路径**/
	private String route;
	/**消息体**/
	private ByteBuf body;
	
	public Integer getMessageId() {
		return messageId;
	}
	
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	
	public String getRoute() {
		return route;
	}
	
	public void setRoute(String route) {
		this.route = route;
	}
	
	public ByteBuf getBody() {
		return body;
	}
	
	public void setBody(ByteBuf body) {
		this.body = body;
	}
}
