package org.tangerine.handler.internal;

import org.tangerine.handler.HandleCommand;
import org.tangerine.protocol.Packet;
import org.tangerine.protocol.model.AuthRequest;
import org.tangerine.protocol.model.AuthResponse;

@HandleCommand(Packet.Type.PCK_AUTH)
public class GateAuthHandler extends AbstractAuthHandler {

	@Override
	protected void postHandle(AuthRequest cmd, AuthResponse response) {
		System.out.println(response);
	}
}
