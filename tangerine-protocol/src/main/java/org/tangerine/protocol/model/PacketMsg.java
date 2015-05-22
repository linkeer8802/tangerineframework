package org.tangerine.protocol.model;

import java.util.Arrays;


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
	private byte[] body;
	
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
	
	public byte[] getBody() {
		return body;
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "PacketMsg [messageId=" + messageId + ", route=" + route
				+ ", body.len=" + body.length + "]";
	}
}
