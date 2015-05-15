package org.tangerine.handler.internal;

import org.tangerine.handler.CommandHandler;
import org.tangerine.net.conn.Connection;
/**
 * 握手Ack
 * @author weird
 *
 */
public final class ShakeACKHandler extends CommandHandler<Void> {

	@Override
	public void handleCmd(Connection conn, Void cmd) {
		conn.setConnected(true);
		//TODO log
		System.out.println("handleShake success.");
	}
}
