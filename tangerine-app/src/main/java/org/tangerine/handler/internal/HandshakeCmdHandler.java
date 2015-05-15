package org.tangerine.handler.internal;

import org.tangerine.common.Constant.Config;
import org.tangerine.container.ComponentManager;
import org.tangerine.handler.CommandHandler;
import org.tangerine.handler.HandleCommand;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.Packet;
import org.tangerine.protocol.TanProtocol;
import org.tangerine.protocol.model.HandShakeRequest;
import org.tangerine.server.Server;

/**
 * 处理握手请求
 * @author weird
 *
 */
@HandleCommand(Packet.Type.PCK_HANDSHAKE)
public final class HandshakeCmdHandler extends CommandHandler<HandShakeRequest> {

	@Override
	public void handleCmd(Connection conn, HandShakeRequest handShakeRequest) {
		int code = 200;
		//检查版本号
		if (!handShakeRequest.getVersion().equals(Config.version)) {
			code = 501;
		}
		//发送握手响应
		//TODO 加载路由字典数据
		conn.deliver(TanProtocol.buildHandshakeResponse(code, 
				ComponentManager.instance().get(Server.class).getHeartbeat(),
				null, null));
	}
}
