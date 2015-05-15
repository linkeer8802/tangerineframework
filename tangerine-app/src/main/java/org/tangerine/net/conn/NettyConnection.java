package org.tangerine.net.conn;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import org.tangerine.protocol.Packet;

public class NettyConnection extends Connection {

	public NettyConnection(Object channel) {
		super(channel);
	}

	@Override
	public void deliver(Packet packet) {
		((Channel) getChannel()).writeAndFlush(packet);
	}

	@Override
	public void deliver(ByteBuf msg) {
		((Channel) getChannel()).writeAndFlush(msg);
	}
	
	@Override
	public Object getAttr(String name) {
		Object attr = super.getAttr(name);
		return attr == null ? ((Channel) getChannel()).attr(AttributeKey.valueOf(name)) : attr;
	}
	
	@Override
	public void addAttr(String name, Object attr) {
		if (((Channel) getChannel()).attr(AttributeKey.valueOf(name)) != null) {
			((Channel) getChannel()).attr(AttributeKey.valueOf(name)).remove();
		}
		super.addAttr(name, attr);
	}
}
