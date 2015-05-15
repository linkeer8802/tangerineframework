package org.tangerine.net.connector;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Set;

import org.tangerine.container.Component;
import org.tangerine.exception.DecodeException;
import org.tangerine.exception.PacketHandleException;
import org.tangerine.handler.Handler;
import org.tangerine.handler.MessageHandler;
import org.tangerine.handler.internal.HandshakeCmdHandler;
import org.tangerine.handler.internal.HeartbeatCmdHandler;
import org.tangerine.handler.internal.ShakeACKHandler;
import org.tangerine.net.conn.Connection;


/**
 * Connector
 * @author weird
 *
 */
public abstract class Connector extends Component {

	protected String host;
	protected Integer port;
	
	protected Set<Handler<?>> handlers = new HashSet<Handler<?>>();
	
	public abstract ByteBuf encode(Integer messageId, String route, Object msg);
	
	public abstract Object decode(ByteBuf buf);
	
	public void bindHandler(Handler<?> handler) {
		handlers.add(handler);
	}
	
	public boolean hasBindedHandler(Class<? extends Handler<?>> handlerClz) {
		for (Handler<?> handler : handlers) {
			if (handlerClz.isAssignableFrom(handler.getClass())) {
				return true;
			}
		}
		
		return false;
	}
	/**
	 * 处理消息
	 * @param msg
	 */
	public final void onData(Connection conn, Object msg) {
		if (msg instanceof ByteBuf) {
			try {
				msg = decode((ByteBuf) msg);
			} catch (Exception e) {
				throw new DecodeException(e);
			}
		}
		
		handler(conn, msg);
	}
	/**
	 * 
	 * @param msg
	 * @throws PacketHandleException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void handler(Connection conn, Object msg) throws PacketHandleException {
		try {
			for (Handler handler : handlers) {
				Class clz = handler.getSuperHandlerGenricType();
				if (msg.getClass().isAssignableFrom(clz)) {
					handler.handle(conn, msg);
				}
			}
		} catch (Exception e) {
			throw new PacketHandleException(e);
		}
	}
	
	@Override
	public void afterStart() {
		if (!hasBindedHandler(MessageHandler.class)) {
			bindHandler(new MessageHandler());
		}
		if (!hasBindedHandler(HandshakeCmdHandler.class)) {
			bindHandler(new HandshakeCmdHandler());
		}
		if (!hasBindedHandler(HeartbeatCmdHandler.class)) {
			bindHandler(new HeartbeatCmdHandler());
		}
		if (!hasBindedHandler(ShakeACKHandler.class)) {
			bindHandler(new ShakeACKHandler());
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
}
