package org.tangerine.net.gate;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.net.connector.NettyIOSocket;

@Sharable
public class NettyGateIOSocket extends NettyIOSocket {
	
	private static final Log log = LogFactory.getLog(NettyGateIOSocket.class);
	
	public synchronized static NettyIOSocket instance() {
		if (instance == null) {
			instance = new NettyGateIOSocket();
		}
		return instance;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		ctx.close();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		log.error(cause.getCause());
		ctx.close();
	}
}
