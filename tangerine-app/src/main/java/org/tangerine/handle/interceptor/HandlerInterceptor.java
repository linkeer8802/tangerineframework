package org.tangerine.handle.interceptor;

import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.model.PacketMsg;

public interface HandlerInterceptor {

	boolean preHandle(Connection conn, PacketMsg message) throws Exception;
	
	void postHandle(Connection conn, PacketMsg message, Object result) throws Exception;
}
