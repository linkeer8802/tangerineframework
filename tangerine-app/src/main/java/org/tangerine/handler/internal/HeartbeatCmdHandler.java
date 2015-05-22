package org.tangerine.handler.internal;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.container.ComponentManager;
import org.tangerine.handler.CommandHandler;
import org.tangerine.handler.HandleCommand;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.Packet;
import org.tangerine.protocol.TanProtocol;
import org.tangerine.server.Server;

/**
 * 心跳包处理
 * @author weird
 *
 */
@HandleCommand(Packet.Type.PCK_HEARTBEAT)
public final class HeartbeatCmdHandler extends CommandHandler<Void> {

	private static final Log log = LogFactory.getLog(HeartbeatCmdHandler.class);
	
	@Override
	public void handleCmd(Connection conn, Void cmd) {
		log.debug("server: recive Heartbeat packet at " + new Date());
		
		int delay = ComponentManager.instance().get(Server.class).getHeartbeat() * 1000;
		conn.delayDeliver(TanProtocol.buildHeartbeatPacket(), delay);
	}
}
