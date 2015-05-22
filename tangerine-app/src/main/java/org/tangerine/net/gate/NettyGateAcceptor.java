package org.tangerine.net.gate;

import io.netty.buffer.ByteBuf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.common.ConfigUtil;
import org.tangerine.container.ComponentDef;
import org.tangerine.container.ComponentManager;
import org.tangerine.exception.DecodeException;
import org.tangerine.handler.internal.GateAuthHandler;
import org.tangerine.net.conn.ConnectionManager;
import org.tangerine.net.connector.NettyConnector;
import org.tangerine.protocol.Packet;

@ComponentDef("nettyGateAcceptor")
public class NettyGateAcceptor extends NettyConnector {

	private static final Log log = LogFactory.getLog(NettyGateAcceptor.class);

	@Override
	public void initialize() throws Exception {
		
		this.host = ConfigUtil.get("tangerine.gate.host", "localhost");
		this.port = ConfigUtil.getInt("tangerine.gate.port", 8770);
		
		ioSocket = NettyGateIOSocket.instance();
		
		connectorThread = new Thread(run());
		connectorThread.setDaemon(true);
		connectorThread.setName(name() + " Thread.");
		
		if (connectionManager == null) {
			connectionManager = new ConnectionManager();
			ComponentManager.instance().addComponent(connectionManager);
		}
	}
	
	@Override
	public ByteBuf encode(Integer messageId, String route, Object msg) {
		throw new UnsupportedOperationException("Unsupported Encode Operation.");
	}

	@Override
	public Object decode(ByteBuf in) {
		//头长度
		if (in.readableBytes() < Packet.LEN_PCK_HEAD) {
			return null;
		}
		//读取头
		in.markReaderIndex();
		Packet packet = null;
		try {
			packet = Packet.decode(in);
		} catch (IndexOutOfBoundsException e) {
			in.resetReaderIndex();
			return null;
		}
		
		/**
		 * 数据包
		 */
		if (!packet.getType().equals(Packet.Type.PCK_AUTH)) {
			throw new DecodeException("Unknown Packet Type：" + packet.getType());
		}
		
		return packet;
	}
	
	@Override
	public void afterStart() {
		if (!hasBindedHandler(GateAuthHandler.class)) {
			bindHandler(new GateAuthHandler());
			log.info("Bind core handler[GateAuthHandler] to "  +name());
		}
	}
}
