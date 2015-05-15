package org.tangerine.handler;

import org.apache.commons.lang.StringUtils;
import org.tangerine.container.ComponentManager;
import org.tangerine.container.impl.RouteDictionary;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.Message;
import org.tangerine.protocol.model.PacketMsg;
import org.tangerine.server.Server;
/**
 * 处理用户消息
 * @author weird
 *
 */
public class MessageHandler extends Handler<Message> {

	@Override
	public void handle(Connection conn, Message msg) throws Exception {
		
		
		PacketMsg pmsg = new PacketMsg();
		
		if (!StringUtils.isEmpty(msg.getRoutePath())
				&& StringUtils.isNumeric(msg.getRoutePath())
				&& ComponentManager.instance().get(Server.class).getUseDict()) {
			pmsg.setRoute(ComponentManager.instance().get(
					RouteDictionary.class).getRoutePath(Short.valueOf(msg.getRoutePath())));
		} else {
			pmsg.setRoute(msg.getRoutePath());
		}
		pmsg.setMessageId(msg.getMessageId());
		pmsg.setBody(msg.getBody()); 
		
		//路由用户消息
		ComponentManager.instance().get(Server.class).getRouter().route(conn, pmsg);
	}
}
