package org.tangerine.handler.internal;

import org.tangerine.handler.HandleCommand;
import org.tangerine.protocol.Packet;
import org.tangerine.protocol.model.AuthRequest;
import org.tangerine.protocol.model.AuthResponse;

@HandleCommand(Packet.Type.PCK_AUTH)
public class ServerAuthHandler extends AbstractAuthHandler {

	@Override
	protected void postHandle(AuthRequest cmd, AuthResponse response) {
		response.addExtra("session", "");
	}
}
