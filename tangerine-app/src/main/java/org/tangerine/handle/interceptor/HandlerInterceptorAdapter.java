package org.tangerine.handle.interceptor;

import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.model.PacketMsg;

public class HandlerInterceptorAdapter implements HandlerInterceptor{

	@Override
	public boolean preHandle(Connection conn, PacketMsg message)
			throws Exception {
		return false;
	}

	@Override
	public void postHandle(Connection conn, PacketMsg message, Object result)
			throws Exception {
		
	}
}
