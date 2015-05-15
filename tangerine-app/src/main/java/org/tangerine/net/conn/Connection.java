package org.tangerine.net.conn;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.tangerine.container.ComponentManager;
import org.tangerine.net.connector.Connector;
import org.tangerine.protocol.Packet;
import org.tangerine.server.Server;

public abstract class Connection {

	protected Object channel;
	
	protected Map<String, Object> attrs;
	
	protected boolean connected;
	
	protected boolean closed;
	
	protected long connectTime;
	
	public Connection(Object channel) {
		this.channel = channel;
		attrs = new HashMap<String, Object>();
	}

	/**
	 * 发送消息
	 * @param packet
	 */
	public abstract void deliver(Packet msg);
	/**
	 * 发送消息
	 * @param packet
	 */
	public abstract void deliver(ByteBuf msg);
	/**
	 * 发送消息
	 * @param messageId 消息Id
	 * @param route 消息路由路径
	 * @param body 消息内容
	 */
	public void deliver(Integer messageId, String route, Object body) {
		deliver(ComponentManager.instance()
				.get(Connector.class).encode(messageId, route, body));
	}
	/**
	 * 延迟(单位为毫秒)发送数据包
	 * @param packet
	 * @param delay
	 */
	public void delayDeliver(final Packet packet, long delay) {
		ComponentManager.instance().get(Server.class)
						.getScheduledExecutor().schedule(new Runnable(){
							@Override
							public void run() {
								deliver(packet);
							}
						}, delay, TimeUnit.MILLISECONDS);
	}
	
	public Object getChannel() {
		return channel;
	}

	public void setChannel(Object channel) {
		this.channel = channel;
	}

	public Object getAttr(String name) {
		return this.attrs.get(name);
	}

	public void addAttr(String name, Object attr) {
		this.attrs.put(name, attr);
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public long getConnectTime() {
		return connectTime;
	}

	public void setConnectTime(long connectTime) {
		this.connectTime = connectTime;
	}
}
