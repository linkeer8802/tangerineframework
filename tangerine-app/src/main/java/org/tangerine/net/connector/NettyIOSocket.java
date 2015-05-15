package org.tangerine.net.connector;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import org.tangerine.container.ComponentManager;
import org.tangerine.net.conn.Connection;
import org.tangerine.net.conn.NettyConnection;

@Sharable
public class NettyIOSocket extends ChannelDuplexHandler {
	
	private static final AttributeKey<Connection> connAttrKey = AttributeKey.valueOf("KEY_CONNECTION");
	
	private static final NettyIOSocket instance = new NettyIOSocket();
	
	
	private NettyIOSocket() {}
	
	public static NettyIOSocket instance() {
		return instance;
	}
	/**
	 * 请求连接
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//新建Connection
		Connection connection = new NettyConnection(ctx.channel());
		ctx.attr(connAttrKey).set(connection);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ComponentManager.instance()
						.get(Connector.class)
						.onData(ctx.attr(connAttrKey).get(), msg);
	}
	/**
	 * 断开连接
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.disconnect();
	}
}
