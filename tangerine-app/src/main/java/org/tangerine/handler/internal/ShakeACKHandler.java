package org.tangerine.handler.internal;

import org.tangerine.handler.CommandHandler;
import org.tangerine.handler.HandleCommand;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.Packet;
/**
 * 握手Ack
 * @author weird
 *
 */
@HandleCommand(Packet.Type.PCK_SHAKE_ACK)
public final class ShakeACKHandler extends CommandHandler<Void> {

	@Override
	public void handleCmd(Connection conn, Void cmd) {
		conn.setConnected(true);
		//TODO log
		System.out.println("handleShake success.");
	}
}
