package org.tangerine.handler.internal;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.container.ComponentManager;
import org.tangerine.container.impl.CoAuthManager;
import org.tangerine.handler.CommandHandler;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.TanProtocol;
import org.tangerine.protocol.model.AuthRequest;
import org.tangerine.protocol.model.AuthResponse;
import org.tangerine.protocol.model.UserInfo;

/**
 * 处理用户认证请求
 * @author weird
 *
 */
public abstract class AbstractAuthHandler extends CommandHandler<AuthRequest> {

	private static final Log log = LogFactory.getLog(AbstractAuthHandler.class);
	
	public void handleCmd(Connection conn, AuthRequest cmd) {
		
		AuthResponse response = auth(cmd);
		
		postHandle(cmd, response);
		
		conn.deliver(TanProtocol.buildAuthResponse(response.getCode(), response.getMsg(), response.getExtras()));
	}

	private AuthResponse auth(AuthRequest cmd) {
		UserInfo userInfo = null;
		AuthResponse response = new AuthResponse();
		
		if (cmd.getToken() != null) {
			userInfo = ComponentManager.instance().get(CoAuthManager.class).getBySId(cmd.getToken());
			
		} else if (cmd.getUname() != null
				&& cmd.getPassword().equals("111111")) {
			
			userInfo = new UserInfo();
			userInfo.setSessionId(UUID.randomUUID().toString());
			userInfo.setUname(cmd.getUname());
			userInfo.setLastLoginTime(System.currentTimeMillis());
			ComponentManager.instance().get(CoAuthManager.class).addUser(userInfo);
			
		} else {
			response.setCode(100);
			response.setMsg("用户名或密码错误");
			
			log.info(cmd.getUname() + "认证失败");
		}
		
		if (userInfo != null) {
			response.setCode(200);
			response.setMsg("认证成功");
			response.addExtra("userInfo", userInfo);
			
			log.info(cmd.getUname() + "认证成功");
		}
		
		return response;
	}
	
	protected abstract void postHandle(AuthRequest cmd, AuthResponse response);
}
