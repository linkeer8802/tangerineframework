package org.tangerine.net.connector;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.container.ComponentManager;
import org.tangerine.net.conn.Connection;
import org.tangerine.net.conn.ConnectionManager;
import org.tangerine.net.conn.NettyConnection;

@Sharable
public class NettyIOSocket extends ChannelDuplexHandler {
	
	private static final Log log = LogFactory.getLog(NettyIOSocket.class);
	
	protected static final AttributeKey<Connection> connAttrKey = AttributeKey.valueOf("KEY_CONNECTION");
	
	protected static NettyIOSocket instance;
	
	
	protected NettyIOSocket() {}
	
	public synchronized static NettyIOSocket instance() {
		if (instance == null) {
			instance = new NettyIOSocket();
		}
		return instance;
	}
	/**
	 * 请求连接
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//新建Connection
		ctx.attr(connAttrKey).set(ComponentManager.instance()
				.get(ConnectionManager.class).addNew(new NettyConnection(ctx.channel())));
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
		log.debug("断开连接" + ctx);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		log.error(cause.getCause());
	}
}
