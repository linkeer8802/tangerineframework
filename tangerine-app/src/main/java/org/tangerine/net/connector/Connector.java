package org.tangerine.net.connector;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.container.Component;
import org.tangerine.exception.DecodeException;
import org.tangerine.exception.PacketHandleException;
import org.tangerine.handler.Handler;
import org.tangerine.handler.MessageHandler;
import org.tangerine.handler.internal.HandshakeCmdHandler;
import org.tangerine.handler.internal.HeartbeatCmdHandler;
import org.tangerine.handler.internal.ServerAuthHandler;
import org.tangerine.handler.internal.ShakeACKHandler;
import org.tangerine.net.conn.Connection;
import org.tangerine.net.conn.ConnectionManager;
import org.tangerine.protocol.Message;
import org.tangerine.protocol.Packet;


/**
 * Connector
 * @author weird
 *
 */
public abstract class Connector extends Component {

	private static final Log log = LogFactory.getLog(Connector.class);
	
	protected String host;
	protected Integer port;
	
	protected ConnectionManager connectionManager;
	
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
		try {
			if (msg instanceof ByteBuf) {
				System.err.println("error get ByteBuf");
				try {
					msg = decode((ByteBuf) msg);
				} catch (Exception e) {
					throw new DecodeException(e);
				}
			}
			
			handler(conn, msg);
		} finally {
			if (msg instanceof Packet) {
				Packet packet = (Packet) msg;
                if (packet.getPayload() != null) {
                	packet.getPayload().release();
                	packet.setPayload(null);
                }
			} else if (msg instanceof Message) {
				Message message = (Message) msg;
                if (message.getBody() != null) {
                	message.getBody().release();
                	message.setBody(null);
                }
			}
		}
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
			log.info("Bind core handler[MessageHandler] to the connector"  + name());
		}
		if (!hasBindedHandler(HandshakeCmdHandler.class)) {
			bindHandler(new HandshakeCmdHandler());
			log.info("Bind core handler[HandshakeCmdHandler] to "  + name());
		}
		if (!hasBindedHandler(HeartbeatCmdHandler.class)) {
			bindHandler(new HeartbeatCmdHandler());
			log.info("Bind core handler[HeartbeatCmdHandler] to "  + name());
		}
		if (!hasBindedHandler(ShakeACKHandler.class)) {
			bindHandler(new ShakeACKHandler());
			log.info("Bind core handler[ShakeACKHandler] to " + name());
		}
		if (!hasBindedHandler(ServerAuthHandler.class)) {
			bindHandler(new ServerAuthHandler());
			log.info("Bind core handler[ServerAuthHandler] to " + name());
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
